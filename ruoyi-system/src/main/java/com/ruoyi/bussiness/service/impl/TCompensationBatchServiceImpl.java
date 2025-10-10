package com.ruoyi.bussiness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.bussiness.domain.*;
import com.ruoyi.bussiness.mapper.*;
import com.ruoyi.bussiness.service.ITCompensationBatchService;
import com.ruoyi.bussiness.service.ITCompensationDetailService;
import com.ruoyi.bussiness.service.ITAppWalletRecordService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 补偿批次Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Service
public class TCompensationBatchServiceImpl extends ServiceImpl<TCompensationBatchMapper, TCompensationBatch> 
        implements ITCompensationBatchService {

    @Autowired
    private TCompensationBatchMapper compensationBatchMapper;
    
    @Autowired
    private TCompensationStrategyMapper compensationStrategyMapper;
    
    @Autowired
    private TSecondContractOrderMapper secondContractOrderMapper;
    
    @Autowired
    private ITCompensationDetailService compensationDetailService;
    
    @Autowired
    private ITAppWalletRecordService walletRecordService;

    @Override
    public List<TCompensationBatch> selectBatchList(TCompensationBatch batch) {
        return compensationBatchMapper.selectBatchListWithStrategy();
    }

    @Override
    public int insertBatch(TCompensationBatch batch) {
        batch.setCreateTime(DateUtils.getNowDate());
        return save(batch) ? 1 : 0;
    }

    @Override
    public int updateBatch(TCompensationBatch batch) {
        batch.setUpdateTime(DateUtils.getNowDate());
        return updateById(batch) ? 1 : 0;
    }

    @Override
    public int deleteBatchByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? ids.length : 0;
    }

    @Override
    public int deleteBatchById(Long id) {
        return removeById(id) ? 1 : 0;
    }

    @Override
    @Transactional
    public TCompensationBatch createCompensationBatch(Long strategyId, String batchName, String symbol, 
                                                     Date startTime, Date endTime, BigDecimal customRate) {
        // 1. 获取策略信息
        TCompensationStrategy strategy = compensationStrategyMapper.selectById(strategyId);
        if (strategy == null) {
            throw new RuntimeException("补偿策略不存在");
        }

        // 2. 搜索符合条件的订单
        LambdaQueryWrapper<TSecondContractOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(TSecondContractOrder::getStatus, 1) // 已开奖
                   .eq(TSecondContractOrder::getRewardAmount, BigDecimal.ZERO) // 没有获奖（全输）
                   .isNull(TSecondContractOrder::getCompensationAmount) // 未补偿过
                   .between(TSecondContractOrder::getCreateTime, startTime, endTime);
        
        // 优先使用传入的symbol参数，如果没有则使用策略中的targetSymbol
        String targetSymbol = StringUtils.isNotBlank(symbol) ? symbol : strategy.getTargetSymbol();
        if (StringUtils.isNotBlank(targetSymbol)) {
            orderWrapper.eq(TSecondContractOrder::getSymbol, targetSymbol);
        }
        
        // 根据策略限制条件过滤
        if (strategy.getMinBetAmount() != null) {
            orderWrapper.ge(TSecondContractOrder::getBetAmount, strategy.getMinBetAmount());
        }
        if (strategy.getMaxBetAmount() != null) {
            orderWrapper.le(TSecondContractOrder::getBetAmount, strategy.getMaxBetAmount());
        }

        List<TSecondContractOrder> orders = secondContractOrderMapper.selectList(orderWrapper);
        
        if (orders.isEmpty()) {
            throw new RuntimeException("没有找到符合条件的订单");
        }

        // 3. 计算统计信息
        Set<Long> userIds = new HashSet<>();
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        BigDecimal compensationRate = customRate != null ? customRate : strategy.getCompensationRate();
        
        for (TSecondContractOrder order : orders) {
            userIds.add(order.getUserId());
            totalBetAmount = totalBetAmount.add(order.getBetAmount());
        }
        
        BigDecimal totalCompensationAmount = totalBetAmount.multiply(compensationRate);

        // 4. 创建补偿批次
        TCompensationBatch batch = new TCompensationBatch();
        batch.setBatchNo("COMP_" + DateUtils.dateTimeNow());
        batch.setBatchName(batchName);
        batch.setStrategyId(strategyId);
        batch.setTotalOrders(orders.size());
        batch.setTotalUsers(userIds.size());
        batch.setTotalBetAmount(totalBetAmount);
        batch.setTotalCompensationAmount(totalCompensationAmount);
        batch.setCompensationRate(compensationRate);
        batch.setStatus(0); // 待执行
        
        // 保存搜索条件
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("symbol", symbol);
        conditions.put("startTime", startTime);
        conditions.put("endTime", endTime);
        conditions.put("strategyId", strategyId);
        batch.setSearchConditions(conditions.toString());
        
        save(batch);

        // 5. 创建补偿详细记录
        List<TCompensationDetail> details = new ArrayList<>();
        for (TSecondContractOrder order : orders) {
            TCompensationDetail detail = new TCompensationDetail();
            detail.setBatchId(batch.getId());
            detail.setOrderId(order.getId());
            detail.setUserId(order.getUserId());
            detail.setUserAddress(order.getUserAddress());
            detail.setOrderNo(order.getOrderNo());
            detail.setSymbol(order.getSymbol());
            detail.setBetAmount(order.getBetAmount());
            detail.setCompensationAmount(order.getBetAmount().multiply(compensationRate));
            detail.setCompensationRate(compensationRate);
            detail.setStatus(0); // 待补偿
            detail.setCreateTime(new Date());
            details.add(detail);
        }
        
        compensationDetailService.saveBatch(details);
        
        return batch;
    }

    @Override
    @Transactional
    public boolean executeCompensationBatch(Long batchId) {
        // 1. 获取批次信息
        TCompensationBatch batch = getById(batchId);
        if (batch == null || batch.getStatus() != 0) {
            return false;
        }

        // 2. 更新批次状态为执行中
        batch.setStatus(1);
        batch.setExecuteTime(new Date());
        updateById(batch);

        try {
            // 3. 获取待补偿的详细记录
            LambdaQueryWrapper<TCompensationDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TCompensationDetail::getBatchId, batchId)
                   .eq(TCompensationDetail::getStatus, 0);
            List<TCompensationDetail> details = compensationDetailService.list(wrapper);

            int successCount = 0;
            int failCount = 0;

            // 4. 执行补偿
            for (TCompensationDetail detail : details) {
                try {
                    // 创建钱包记录
                    TAppWalletRecord walletRecord = new TAppWalletRecord();
                    walletRecord.setUserId(detail.getUserId());
                    walletRecord.setAmount(detail.getCompensationAmount());
                    walletRecord.setBeforeAmount(BigDecimal.ZERO); // 补偿前金额
                    walletRecord.setAfterAmount(detail.getCompensationAmount()); // 补偿后金额
                    walletRecord.setType(10); // 补偿类型
                    walletRecord.setSymbol("USDT");
                    walletRecord.setRemark("订单补偿：" + detail.getOrderNo());
                    walletRecord.setCreateTime(new Date());
                    walletRecord.setOperateTime(new Date());
                    
                    boolean walletResult = walletRecordService.save(walletRecord);
                    
                    if (walletResult) {
                        // 更新订单的补偿金额
                        TSecondContractOrder order = secondContractOrderMapper.selectById(detail.getOrderId());
                        if (order != null) {
                            order.setCompensationAmount(detail.getCompensationAmount());
                            secondContractOrderMapper.updateById(order);
                        }
                        
                        // 更新补偿详情状态
                        detail.setStatus(1); // 已补偿
                        detail.setCompensationTime(new Date());
                        detail.setWalletRecordId(walletRecord.getId());
                        successCount++;
                    } else {
                        detail.setStatus(2); // 补偿失败
                        detail.setErrorMsg("钱包记录创建失败");
                        failCount++;
                    }
                } catch (Exception e) {
                    detail.setStatus(2); // 补偿失败
                    detail.setErrorMsg("补偿异常：" + e.getMessage());
                    failCount++;
                }
                
                compensationDetailService.updateById(detail);
            }

            // 5. 更新批次状态
            batch.setStatus(2); // 已完成
            batch.setCompleteTime(new Date());
            batch.setRemark("成功：" + successCount + "，失败：" + failCount);
            updateById(batch);

            return true;
        } catch (Exception e) {
            // 执行失败，回滚批次状态
            batch.setStatus(0); // 回到待执行
            batch.setExecuteTime(null);
            batch.setRemark("执行失败：" + e.getMessage());
            updateById(batch);
            return false;
        }
    }

    @Override
    public boolean cancelCompensationBatch(Long batchId) {
        TCompensationBatch batch = getById(batchId);
        if (batch == null || batch.getStatus() != 0) {
            return false;
        }
        
        batch.setStatus(3); // 已取消
        batch.setUpdateTime(new Date());
        return updateById(batch);
    }

    @Override
    public Map<String, Object> searchOrdersPreview(String symbol, Date startTime, Date endTime) {
        LambdaQueryWrapper<TSecondContractOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TSecondContractOrder::getStatus, 1) // 已开奖
               .eq(TSecondContractOrder::getRewardAmount, BigDecimal.ZERO) // 没有获奖
               .isNull(TSecondContractOrder::getCompensationAmount) // 未补偿过
               .between(TSecondContractOrder::getCreateTime, startTime, endTime);
        
        if (StringUtils.isNotBlank(symbol)) {
            wrapper.eq(TSecondContractOrder::getSymbol, symbol);
        }

        List<TSecondContractOrder> orders = secondContractOrderMapper.selectList(wrapper);
        
        Set<Long> userIds = new HashSet<>();
        BigDecimal totalBetAmount = BigDecimal.ZERO;
        
        for (TSecondContractOrder order : orders) {
            userIds.add(order.getUserId());
            totalBetAmount = totalBetAmount.add(order.getBetAmount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderCount", orders.size());
        result.put("userCount", userIds.size());
        result.put("totalBetAmount", totalBetAmount);
        result.put("orders", orders.size() > 100 ? orders.subList(0, 100) : orders); // 最多返回100条预览
        
        return result;
    }
}
