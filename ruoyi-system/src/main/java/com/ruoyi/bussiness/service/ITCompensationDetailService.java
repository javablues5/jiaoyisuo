package com.ruoyi.bussiness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.bussiness.domain.TCompensationDetail;

import java.util.List;
import java.util.Map;

/**
 * 补偿详细记录Service接口
 *
 * @author ruoyi
 * @date 2024-09-27
 */
public interface ITCompensationDetailService extends IService<TCompensationDetail> {

    /**
     * 查询补偿详细记录列表
     *
     * @param detail 补偿详细记录
     * @return 补偿详细记录集合
     */
    List<TCompensationDetail> selectDetailList(TCompensationDetail detail);

    /**
     * 根据批次ID查询补偿详细记录
     *
     * @param batchId 批次ID
     * @return 补偿详细记录集合
     */
    List<TCompensationDetail> selectDetailListByBatchId(Long batchId);

    /**
     * 新增补偿详细记录
     *
     * @param detail 补偿详细记录
     * @return 结果
     */
    int insertDetail(TCompensationDetail detail);

    /**
     * 修改补偿详细记录
     *
     * @param detail 补偿详细记录
     * @return 结果
     */
    int updateDetail(TCompensationDetail detail);

    /**
     * 批量删除补偿详细记录
     *
     * @param ids 需要删除的补偿详细记录主键集合
     * @return 结果
     */
    int deleteDetailByIds(Long[] ids);

    /**
     * 删除补偿详细记录信息
     *
     * @param id 补偿详细记录主键
     * @return 结果
     */
    int deleteDetailById(Long id);

    /**
     * 获取批次补偿状态统计
     *
     * @param batchId 批次ID
     * @return 状态统计
     */
    Map<String, Object> getCompensationStatistics(Long batchId);

    /**
     * 重新补偿失败的记录
     *
     * @param detailIds 详细记录ID数组
     * @return 结果
     */
    boolean retryFailedCompensation(Long[] detailIds);
}
