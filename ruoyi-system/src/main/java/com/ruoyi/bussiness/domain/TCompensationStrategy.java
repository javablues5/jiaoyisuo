package com.ruoyi.bussiness.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 补偿策略配置对象 t_compensation_strategy
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_compensation_strategy")
public class TCompensationStrategy extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略名称
     */
    @Excel(name = "策略名称")
    private String strategyName;

    /**
     * 策略描述
     */
    @Excel(name = "策略描述")
    private String strategyDesc;

    /**
     * 补偿比例(0-1)
     */
    @Excel(name = "补偿比例")
    private BigDecimal compensationRate;

    /**
     * 最小投注金额限制
     */
    @Excel(name = "最小投注金额")
    private BigDecimal minBetAmount;

    /**
     * 最大投注金额限制
     */
    @Excel(name = "最大投注金额")
    private BigDecimal maxBetAmount;

    /**
     * 目标交易对(为空表示全部)
     */
    @Excel(name = "目标交易对")
    private String targetSymbol;

    /**
     * 目标周期(秒,为空表示全部)
     */
    @Excel(name = "目标周期")
    private Integer targetPeriod;

    /**
     * 状态(1:启用 0:禁用)
     */
    @Excel(name = "状态", dictType = "sys_normal_disable")
    private Integer status;
}
