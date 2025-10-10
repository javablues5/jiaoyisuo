package com.ruoyi.bussiness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.bussiness.domain.TCompensationStrategy;
import com.ruoyi.bussiness.mapper.TCompensationStrategyMapper;
import com.ruoyi.bussiness.service.ITCompensationStrategyService;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 补偿策略配置Service业务层处理
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Service
public class TCompensationStrategyServiceImpl extends ServiceImpl<TCompensationStrategyMapper, TCompensationStrategy> 
        implements ITCompensationStrategyService {

    @Override
    public List<TCompensationStrategy> selectStrategyList(TCompensationStrategy strategy) {
        LambdaQueryWrapper<TCompensationStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(strategy.getStrategyName()), TCompensationStrategy::getStrategyName, strategy.getStrategyName())
               .eq(strategy.getStatus() != null, TCompensationStrategy::getStatus, strategy.getStatus())
               .like(StringUtils.isNotBlank(strategy.getTargetSymbol()), TCompensationStrategy::getTargetSymbol, strategy.getTargetSymbol())
               .orderByDesc(TCompensationStrategy::getCreateTime);
        return list(wrapper);
    }

    @Override
    public int insertStrategy(TCompensationStrategy strategy) {
        return save(strategy) ? 1 : 0;
    }

    @Override
    public int updateStrategy(TCompensationStrategy strategy) {
        return updateById(strategy) ? 1 : 0;
    }

    @Override
    public int deleteStrategyByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? ids.length : 0;
    }

    @Override
    public int deleteStrategyById(Long id) {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public List<TCompensationStrategy> selectEnabledStrategies() {
        LambdaQueryWrapper<TCompensationStrategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TCompensationStrategy::getStatus, 1)
               .orderByDesc(TCompensationStrategy::getCreateTime);
        return list(wrapper);
    }
}
