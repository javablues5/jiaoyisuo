package com.ruoyi.bussiness.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
public class TAgentActivityInfoVo {

    /**
     * id
     */
    private String id;
    /**
     * 1 充值返利 2挖矿返利
     */
    private Integer type;

    /**
     * 1usdt-erc 2 usdt-trc 3btc 4eth
     */
    private String coinType;
    /**
     * 返利用户
     */
    private Long fromId;
    private String loginName;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 1  待返  2  已返
     */
    private Integer status;

    /**
     * $column.columnComment
     */
    private String serialId;

    private BigDecimal sumAmount;

    @TableField(exist = false)
    private BigDecimal czAmount;
    @TableField(exist = false)
    private BigDecimal txAmount;

    @CreatedDate
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Map<String,Object> params;
}
