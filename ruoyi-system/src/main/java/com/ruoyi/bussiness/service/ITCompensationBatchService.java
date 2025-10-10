package com.ruoyi.bussiness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.bussiness.domain.TCompensationBatch;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 补偿批次Service接口
 *
 * @author ruoyi
 * @date 2024-09-27
 */
public interface ITCompensationBatchService extends IService<TCompensationBatch> {

    /**
     * 查询补偿批次列表
     *
     * @param batch 补偿批次
     * @return 补偿批次集合
     */
    List<TCompensationBatch> selectBatchList(TCompensationBatch batch);

    /**
     * 新增补偿批次
     *
     * @param batch 补偿批次
     * @return 结果
     */
    int insertBatch(TCompensationBatch batch);

    /**
     * 修改补偿批次
     *
     * @param batch 补偿批次
     * @return 结果
     */
    int updateBatch(TCompensationBatch batch);

    /**
     * 批量删除补偿批次
     *
     * @param ids 需要删除的补偿批次主键集合
     * @return 结果
     */
    int deleteBatchByIds(Long[] ids);

    /**
     * 删除补偿批次信息
     *
     * @param id 补偿批次主键
     * @return 结果
     */
    int deleteBatchById(Long id);

    /**
     * 搜索符合条件的订单并创建补偿批次
     *
     * @param strategyId 策略ID
     * @param batchName 批次名称
     * @param symbol 交易对
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param customRate 自定义补偿比例（可选）
     * @return 补偿批次
     */
    TCompensationBatch createCompensationBatch(Long strategyId, String batchName, String symbol, 
                                               Date startTime, Date endTime, BigDecimal customRate);

    /**
     * 执行补偿批次
     *
     * @param batchId 批次ID
     * @return 结果
     */
    boolean executeCompensationBatch(Long batchId);

    /**
     * 取消补偿批次
     *
     * @param batchId 批次ID
     * @return 结果
     */
    boolean cancelCompensationBatch(Long batchId);

    /**
     * 搜索符合条件的订单（预览）
     *
     * @param symbol 交易对
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 搜索结果统计
     */
    Map<String, Object> searchOrdersPreview(String symbol, Date startTime, Date endTime);
}
