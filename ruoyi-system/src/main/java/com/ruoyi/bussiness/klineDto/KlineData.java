package com.ruoyi.bussiness.klineDto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

// K线内部数据对象
@Data
@ToString
public class KlineData {

    // 主要关注计算所需的字段，使用 BigDecimal 确保精度
    private String s;           // 交易对 (s)
    private String i;           // K线间隔 (i)
    private boolean x;          // 是否完结 (x)

    // 价格数据，使用 BigDecimal
    private BigDecimal o;       // 开盘价 (o)
    private BigDecimal c;       // 收盘价 (c)
    private BigDecimal h;       // 最高价 (h)
    private BigDecimal l;       // 最低价 (l)

    // 时间和数量等其他字段
    private Long t;             // K线起始时间 (t)
    private Long T;             // K线结束时间 (T)
    private BigDecimal v;       // 成交量 (v)
    private BigDecimal q;       // 成交额 (q)
    private BigDecimal V;       // 主动买入的成交量 (V)
    private BigDecimal Q;       // 主动买入的成交额 (Q)

    // 其他字段
    private Long f;  //id  这两个id不用管
    private Long L;  //id
    private String B; // 忽略此参数
    private Integer n; // 成交数量

}
