package com.ruoyi.bussiness.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.ruoyi.bussiness.domain.TAgentActivityInfo;
import com.ruoyi.bussiness.domain.TAppUser;
import com.ruoyi.bussiness.domain.TAppWalletRecord;
import com.ruoyi.bussiness.domain.vo.TAgentActivityInfoVo;
import com.ruoyi.bussiness.mapper.TAgentActivityInfoMapper;
import com.ruoyi.bussiness.service.ITAgentActivityInfoService;
import com.ruoyi.common.crud.MpCrudTool;
import com.ruoyi.common.utils.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 返利活动明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-07-06
 */
@Service
public class TAgentActivityInfoServiceImpl extends ServiceImpl<TAgentActivityInfoMapper, TAgentActivityInfo> implements ITAgentActivityInfoService
{
    @Autowired
    private TAgentActivityInfoMapper tAgentActivityInfoMapper;
    @Autowired
    private TAppWalletRecordServiceImpl tAppWalletRecordService;

    /**
     * 查询返利活动明细
     * 
     * @param id 返利活动明细主键
     * @return 返利活动明细
     */
    @Override
    public TAgentActivityInfo selectTAgentActivityInfoById(String id)
    {
        return tAgentActivityInfoMapper.selectTAgentActivityInfoById(id);
    }

    /**
     * 查询返利活动明细列表
     * 
     * @param tAgentActivityInfo 返利活动明细
     * @return 返利活动明细
     */
    @Override
    public List<TAgentActivityInfo> selectTAgentActivityInfoList(TAgentActivityInfo tAgentActivityInfo)
    {
        return tAgentActivityInfoMapper.selectTAgentActivityInfoList(tAgentActivityInfo);
    }

    /**
     * 新增返利活动明细
     * 
     * @param tAgentActivityInfo 返利活动明细
     * @return 结果
     */
    @Override
    public int insertTAgentActivityInfo(TAgentActivityInfo tAgentActivityInfo)
    {
        tAgentActivityInfo.setCreateTime(DateUtils.getNowDate());
        return tAgentActivityInfoMapper.insertTAgentActivityInfo(tAgentActivityInfo);
    }

    /**
     * 修改返利活动明细
     * 
     * @param tAgentActivityInfo 返利活动明细
     * @return 结果
     */
    @Override
    public int updateTAgentActivityInfo(TAgentActivityInfo tAgentActivityInfo)
    {
        tAgentActivityInfo.setUpdateTime(DateUtils.getNowDate());
        return tAgentActivityInfoMapper.updateTAgentActivityInfo(tAgentActivityInfo);
    }

    /**
     * 批量删除返利活动明细
     * 
     * @param ids 需要删除的返利活动明细主键
     * @return 结果
     */
    @Override
    public int deleteTAgentActivityInfoByIds(String[] ids)
    {
        return tAgentActivityInfoMapper.deleteTAgentActivityInfoByIds(ids);
    }

    /**
     * 删除返利活动明细信息
     * 
     * @param id 返利活动明细主键
     * @return 结果
     */
    @Override
    public int deleteTAgentActivityInfoById(String id)
    {
        return tAgentActivityInfoMapper.deleteTAgentActivityInfoById(id);
    }

    @Override
    public int selectListByLeve(TAgentActivityInfo tAgentActivityInfo) {
        return tAgentActivityInfoMapper.selectListByLeve(tAgentActivityInfo);
    }
    private static final List<String> excludeFields = Arrays.asList("login_password","search_value", "params", "update_by","create_by","update_time","remark");
    @Override
    public Map<Object, Object> selectUserActivityInfo(TAgentActivityInfo tAgentActivityInfo) {
        //查询1 2 3 级
        tAgentActivityInfo.setType(1);
        tAgentActivityInfo.setStatus(2);
//        Map<String, Object> params = new HashMap<>();
//        params.put("leve",1);
//        tAgentActivityInfo.setParams(params);
//        int oneCount = tAgentActivityInfoMapper.selectListByLeve(tAgentActivityInfo);
//        params.put("leve",2);
//        int twoCount = tAgentActivityInfoMapper.selectListByLeve(tAgentActivityInfo);
//        params.put("leve",3);
//        int threeCount = tAgentActivityInfoMapper.selectListByLeve(tAgentActivityInfo);
        Map<Object, Object> resultMap = new HashMap<>();
//        resultMap.put("oneCount",oneCount);
//        resultMap.put("twoCount",twoCount);
//        resultMap.put("threeCount",threeCount);
//        resultMap.put("sumCount",oneCount+twoCount+threeCount);





        TAppUser u = new TAppUser();
        //u.setAppParentIds(tAgentActivityInfo.getUserId().toString());
        //List<TAppUser> tAppUsers = tAgentActivityInfoMapper.selectByCommaSeparatedIds(u);

        Map<String, Object> params1 = getStringObjectMap(tAgentActivityInfo);
        //params1.put("begin_time",startOfDay);
        //params1.put("end_time",endOfDay);

        u.setParams(params1);

        List<TAppUser> tAppUsers1 = MpCrudTool.select_apply_List(u, TAppUser.class, excludeFields, params1);
        // 1. 获取今天的日期部分
        final LocalDate today = LocalDate.now();
        long todayCount = tAppUsers1.stream()
                .filter(user -> {
                    LocalDateTime createTime = user.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    return createTime != null && createTime.toLocalDate().isEqual(today);
                })
                // 计数
                .count();


        resultMap.put("todayCount",todayCount);
        resultMap.put("sumCount",tAppUsers1.size());
        BigDecimal sumAmount = tAgentActivityInfoMapper.selectAmountCountByUserId(tAgentActivityInfo);
        sumAmount = Objects.isNull(sumAmount) ? BigDecimal.ZERO : sumAmount;
        resultMap.put("sumAmount",sumAmount);



        return resultMap;
    }

    @NotNull
    private Map<String, Object> getStringObjectMap(TAgentActivityInfo tAgentActivityInfo) {
        Map<String, Object> params1 = tAgentActivityInfo.getParams();
        params1 = params1==null? new HashMap<>():params1;
        params1.put("apply","FIND_IN_SET('"+ tAgentActivityInfo.getUserId()+"', app_parent_ids) > 0");

        // 1. 获取今天的开始时间 (00:00:00)
        //LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        // 2. 获取今天的结束时间 (23:59:59.999999...)
        //LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        params1.put("column","create_time");//createTime
        return params1;
    }

//    @Override
//    public List<TAgentActivityInfoVo> getAgentList(TAgentActivityInfo tAgentActivityInfo) {
//        return tAgentActivityInfoMapper.getAgentList(tAgentActivityInfo);
//    }

    @Override
    public List<TAgentActivityInfoVo> getAgentList(TAgentActivityInfo tAgentActivityInfo) {
        List<TAgentActivityInfoVo> agentList = tAgentActivityInfoMapper.getAgentList(tAgentActivityInfo);
        List<Long> ids = agentList.stream().map(TAgentActivityInfoVo::getFromId).collect(Collectors.toList());
        // 要查询的 type 列表
        List<Integer> types = new ArrayList<>();
        types.add(1);
        types.add(51);
        //提现
        List<Integer> types2 = new ArrayList<>();
        types2.add(2);
        types2.add(4);
        types2.add(8);
        //秒合约下注
        List<Integer> types3 = new ArrayList<>();
        types3.add(9);

        Map<Long, List<TAppWalletRecord>> cz = extracted(ids, types, "SUM(u_amount) as u_amount");
        Map<Long, List<TAppWalletRecord>> tx = extracted(ids, types2, "SUM(u_amount) as u_amount");
        Map<Long, List<TAppWalletRecord>> sy = extracted(ids, types3, "SUM(u_amount) as u_amount");
        agentList
                .forEach(agentVo -> {
                    List<TAppWalletRecord> records = cz.get(agentVo.getFromId());
                    BigDecimal u = records!=null?records.get(0).getUAmount():BigDecimal.ZERO;
                    agentVo.setCzAmount(u);

                    List<TAppWalletRecord> re = tx.get(agentVo.getFromId());
                    BigDecimal u2 = re!=null?re.get(0).getUAmount():BigDecimal.ZERO;
                    agentVo.setTxAmount(u2);

                    //收益
                    List<TAppWalletRecord> re3 = sy.get(agentVo.getFromId());
                    BigDecimal u3 = re3!=null?re3.get(0).getUAmount():BigDecimal.ZERO;
                    agentVo.setSumAmount(u3);
                });


        return agentList;
    }

    private Map<Long, List<TAppWalletRecord>> extracted(List<Long> ids, List<Integer> types, String sum) {
        QueryWrapper<TAppWalletRecord> wrapper = new QueryWrapper<>();
        // 这里的参数 "wallet_amount" 是数据库的字段名
        wrapper.select("user_id","symbol", sum);

        // 3. 使用 IN 条件进行批量过滤
        // 这里的参数 "user_id" 是数据库的字段名
        wrapper.in("user_id", ids);
        wrapper.in("type", types); // 使用 List 传入多个值
        wrapper.eq("symbol","usdt");
        wrapper.groupBy("user_id", "symbol");
        List<TAppWalletRecord> list = tAppWalletRecordService.list(wrapper);
        return list.stream()
                .collect(Collectors.groupingBy(
                        TAppWalletRecord::getUserId  // Key: 使用 getUserId() 作为分组键
                ));
    }

}
