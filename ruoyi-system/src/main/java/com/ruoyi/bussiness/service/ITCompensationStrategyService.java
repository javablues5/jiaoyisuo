package com.ruoyi.bussiness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.bussiness.domain.TCompensationStrategy;

import java.util.List;

/**
 * 补偿策略配置Service接口
 *
 * @author ruoyi
 * @date 2024-09-27
 */
public interface ITCompensationStrategyService extends IService<TCompensationStrategy> {

    /**
     * 查询补偿策略配置列表
     *
     * @param strategy 补偿策略配置
     * @return 补偿策略配置集合
     */
    List<TCompensationStrategy> selectStrategyList(TCompensationStrategy strategy);

    /**
     * 新增补偿策略配置
     *
     * @param strategy 补偿策略配置
     * @return 结果
     */
    int insertStrategy(TCompensationStrategy strategy);

    /**
     * 修改补偿策略配置
     *
     * @param strategy 补偿策略配置
     * @return 结果
     */
    int updateStrategy(TCompensationStrategy strategy);

    /**
     * 批量删除补偿策略配置
     *
     * @param ids 需要删除的补偿策略配置主键集合
     * @return 结果
     */
    int deleteStrategyByIds(Long[] ids);

    /**
     * 删除补偿策略配置信息
     *
     * @param id 补偿策略配置主键
     * @return 结果
     */
    int deleteStrategyById(Long id);

    /**
     * 获取启用的策略列表
     *
     * @return 启用的策略列表
     */
    List<TCompensationStrategy> selectEnabledStrategies();
}
