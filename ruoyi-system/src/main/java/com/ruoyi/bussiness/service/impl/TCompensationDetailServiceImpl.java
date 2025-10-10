package com.ruoyi.bussiness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.bussiness.domain.TCompensationDetail;
import com.ruoyi.bussiness.domain.TAppWalletRecord;
import com.ruoyi.bussiness.mapper.TCompensationDetailMapper;
import com.ruoyi.bussiness.service.ITCompensationDetailService;
import com.ruoyi.bussiness.service.ITAppWalletRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 补偿详细记录Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Service
public class TCompensationDetailServiceImpl extends ServiceImpl<TCompensationDetailMapper, TCompensationDetail> 
        implements ITCompensationDetailService {

    @Autowired
    private TCompensationDetailMapper compensationDetailMapper;
    
    @Autowired
    private ITAppWalletRecordService walletRecordService;

    @Override
    public List<TCompensationDetail> selectDetailList(TCompensationDetail detail) {
        LambdaQueryWrapper<TCompensationDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(detail.getBatchId() != null, TCompensationDetail::getBatchId, detail.getBatchId())
               .eq(detail.getUserId() != null, TCompensationDetail::getUserId, detail.getUserId())
               .eq(detail.getStatus() != null, TCompensationDetail::getStatus, detail.getStatus())
               .orderByDesc(TCompensationDetail::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TCompensationDetail> selectDetailListByBatchId(Long batchId) {
        return compensationDetailMapper.selectDetailListWithUserInfo(batchId);
    }

    @Override
    public int insertDetail(TCompensationDetail detail) {
        detail.setCreateTime(new Date());
        return save(detail) ? 1 : 0;
    }

    @Override
    public int updateDetail(TCompensationDetail detail) {
        return updateById(detail) ? 1 : 0;
    }

    @Override
    public int deleteDetailByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? ids.length : 0;
    }

    @Override
    public int deleteDetailById(Long id) {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public Map<String, Object> getCompensationStatistics(Long batchId) {
        List<Map<String, Object>> statusStats = compensationDetailMapper.selectStatusStatistics(batchId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("pending", 0);   // 待补偿
        result.put("success", 0);   // 已补偿
        result.put("failed", 0);    // 补偿失败
        
        for (Map<String, Object> stat : statusStats) {
            Integer status = (Integer) stat.get("status");
            Long count = (Long) stat.get("count");
            
            result.put("total", (Integer) result.get("total") + count.intValue());
            
            switch (status) {
                case 0:
                    result.put("pending", count.intValue());
                    break;
                case 1:
                    result.put("success", count.intValue());
                    break;
                case 2:
                    result.put("failed", count.intValue());
                    break;
            }
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean retryFailedCompensation(Long[] detailIds) {
        List<TCompensationDetail> details = listByIds(Arrays.asList(detailIds));
        
        for (TCompensationDetail detail : details) {
            if (detail.getStatus() != 2) { // 只重试失败的记录
                continue;
            }
            
            try {
                // 创建钱包记录
                TAppWalletRecord walletRecord = new TAppWalletRecord();
                walletRecord.setUserId(detail.getUserId());
                walletRecord.setAmount(detail.getCompensationAmount());
                walletRecord.setBeforeAmount(BigDecimal.ZERO); // 补偿前金额
                walletRecord.setAfterAmount(detail.getCompensationAmount()); // 补偿后金额
                walletRecord.setType(10); // 补偿类型
                walletRecord.setSymbol("USDT");
                walletRecord.setRemark("订单补偿重试：" + detail.getOrderNo());
                walletRecord.setCreateTime(new Date());
                walletRecord.setOperateTime(new Date());
                
                boolean walletResult = walletRecordService.save(walletRecord);
                
                if (walletResult) {
                    detail.setStatus(1); // 已补偿
                    detail.setCompensationTime(new Date());
                    detail.setWalletRecordId(walletRecord.getId());
                    detail.setErrorMsg(null);
                } else {
                    detail.setErrorMsg("重试失败：钱包记录创建失败");
                }
            } catch (Exception e) {
                detail.setErrorMsg("重试异常：" + e.getMessage());
            }
            
            updateById(detail);
        }
        
        return true;
    }
}
