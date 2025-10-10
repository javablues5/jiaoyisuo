package com.ruoyi.bussiness.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 补偿详细记录对象 t_compensation_detail
 *
 * @author ruoyi
 * @date 2024-09-27
 */
@Data
@TableName("t_compensation_detail")
public class TCompensationDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 批次ID
     */
    private Long batchId;

    /**
     * 订单ID
     */
    @Excel(name = "订单ID")
    private Long orderId;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 用户地址
     */
    @Excel(name = "用户地址")
    private String userAddress;

    /**
     * 订单号
     */
    @Excel(name = "订单号")
    private String orderNo;

    /**
     * 交易对
     */
    @Excel(name = "交易对")
    private String symbol;

    /**
     * 投注金额
     */
    @Excel(name = "投注金额")
    private BigDecimal betAmount;

    /**
     * 补偿金额
     */
    @Excel(name = "补偿金额")
    private BigDecimal compensationAmount;

    /**
     * 补偿比例
     */
    @Excel(name = "补偿比例")
    private BigDecimal compensationRate;

    /**
     * 补偿状态(0:待补偿 1:已补偿 2:补偿失败)
     */
    @Excel(name = "补偿状态", dictType = "compensation_detail_status")
    private Integer status;

    /**
     * 补偿时间
     */
    @Excel(name = "补偿时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date compensationTime;

    /**
     * 钱包记录ID
     */
    private Long walletRecordId;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    // 非数据库字段 - 用于前端显示
    @TableField(exist = false)
    private String loginName;  // 用户名

    @TableField(exist = false)
    private String batchName;  // 批次名称
}
