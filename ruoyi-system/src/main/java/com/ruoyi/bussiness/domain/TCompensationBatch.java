package com.ruoyi.bussiness.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 补偿批次对象 t_compensation_batch
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_compensation_batch")
public class TCompensationBatch extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次号
     */
    @Excel(name = "批次号")
    private String batchNo;

    /**
     * 批次名称
     */
    @Excel(name = "批次名称")
    private String batchName;

    /**
     * 使用的策略ID
     */
    @Excel(name = "策略ID")
    private Long strategyId;

    /**
     * 搜索条件(JSON格式)
     */
    private String searchConditions;

    /**
     * 符合条件的订单总数
     */
    @Excel(name = "订单总数")
    private Integer totalOrders;

    /**
     * 涉及用户总数
     */
    @Excel(name = "用户总数")
    private Integer totalUsers;

    /**
     * 总投注金额
     */
    @Excel(name = "总投注金额")
    private BigDecimal totalBetAmount;

    /**
     * 总补偿金额
     */
    @Excel(name = "总补偿金额")
    private BigDecimal totalCompensationAmount;

    /**
     * 实际使用的补偿比例
     */
    @Excel(name = "补偿比例")
    private BigDecimal compensationRate;

    /**
     * 状态(0:待执行 1:执行中 2:已完成 3:已取消)
     */
    @Excel(name = "状态", dictType = "compensation_batch_status")
    private Integer status;

    /**
     * 执行时间
     */
    @Excel(name = "执行时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /**
     * 完成时间
     */
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    // 非数据库字段 - 用于前端显示
    @TableField(exist = false)
    private String strategyName;
}
