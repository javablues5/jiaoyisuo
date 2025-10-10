package com.ruoyi.bussiness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.bussiness.domain.TCompensationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 补偿详细记录Mapper接口
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Mapper
public interface TCompensationDetailMapper extends BaseMapper<TCompensationDetail> {

    /**
     * 查询补偿详细记录列表，包含用户名和批次名称
     */
    @Select("SELECT d.*, u.login_name, b.batch_name " +
            "FROM t_compensation_detail d " +
            "LEFT JOIN t_app_user u ON d.user_id = u.user_id " +
            "LEFT JOIN t_compensation_batch b ON d.batch_id = b.id " +
            "WHERE d.batch_id = #{batchId} " +
            "ORDER BY d.create_time DESC")
    List<TCompensationDetail> selectDetailListWithUserInfo(@Param("batchId") Long batchId);

    /**
     * 统计补偿详情状态
     */
    @Select("SELECT status, COUNT(*) as count FROM t_compensation_detail " +
            "WHERE batch_id = #{batchId} GROUP BY status")
    List<java.util.Map<String, Object>> selectStatusStatistics(@Param("batchId") Long batchId);
}
