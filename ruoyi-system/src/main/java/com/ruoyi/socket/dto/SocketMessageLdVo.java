package com.ruoyi.socket.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_ld_socket")
public class SocketMessageLdVo extends BaseEntity {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    private String type;
    private String logo;
    private String name;
    private String time;
    private BigDecimal percentage;
    //@TableField("now_time")
    private Long nowTime;

}
