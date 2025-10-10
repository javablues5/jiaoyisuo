package com.ruoyi.bussiness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.bussiness.domain.TCompensationBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 补偿批次Mapper接口
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Mapper
public interface TCompensationBatchMapper extends BaseMapper<TCompensationBatch> {

    /**
     * 查询补偿批次列表，包含策略名称
     */
    @Select("SELECT b.*, s.strategy_name FROM t_compensation_batch b " +
            "LEFT JOIN t_compensation_strategy s ON b.strategy_id = s.id " +
            "ORDER BY b.create_time DESC")
    List<TCompensationBatch> selectBatchListWithStrategy();

    /**
     * 根据ID查询补偿批次，包含策略名称
     */
    @Select("SELECT b.*, s.strategy_name FROM t_compensation_batch b " +
            "LEFT JOIN t_compensation_strategy s ON b.strategy_id = s.id " +
            "WHERE b.id = #{id}")
    TCompensationBatch selectBatchWithStrategy(@Param("id") Long id);
}
