package com.ruoyi.websocket.sevice;

import com.alibaba.fastjson.JSON;
import com.ruoyi.bussiness.klineDto.KlineData;
import com.ruoyi.common.crud.MpCrudTool;
import com.ruoyi.socket.dto.SocketMessageLdVo;
import com.ruoyi.socket.socketserver.WebSocketLeiDa;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LDSevice {


    // 目标阈值：5%
    private static final BigDecimal THRESHOLD = new BigDecimal("0.0003");

    /**
     * 计算涨跌幅并判断是否触发雷达
     * @param klineData K线内部数据对象
     */
    //@Async("ldSocketThread")
    @Async()
    public void checkPriceChange(KlineData klineData, String time) {
        // 1. 确保 K 线已完结 (仅对完结的 K 线进行计算)
        // 如果 x 为 false，表示 K 线还在更新中，暂不作为雷达信号
        if (!klineData.isX()) {
            return;
        }

        BigDecimal open = klineData.getO();
        BigDecimal close = klineData.getC();

        // 确保开盘价不是零，避免除以零错误
        if (open.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // 2. 简单计算涨跌幅
        // 计算公式： (close - open) / open
        BigDecimal change = close.subtract(open);
        BigDecimal rate = change.divide(open, 4, RoundingMode.HALF_UP); // 保留4位小数

        // 转换为百分比 展示的（例如 0.05 -> 5.0%）
        BigDecimal percentage = rate.multiply(BigDecimal.valueOf(100));

        // 3. 判断是否触发雷达 (绝对值 > 5%)
        // rate的值可能是 上涨或 下跌 负数， abs()就是把负号去掉，方便与 THRESHOLD 进行判断的
        BigDecimal absRate = rate.abs(); // 取绝对值

        if (absRate.compareTo(THRESHOLD) >= 0) {
            String direction = (change.compareTo(BigDecimal.ZERO) > 0) ? "上涨" : "下跌";
            String msg = String.format("%s %.2f%%%n\n价格在过去5分钟内%s %.2f%%%n",
                    klineData.getS(), percentage, direction, percentage);

            System.out.printf("🚨 雷达触发! 交易对: %s, 方向: %s, 幅度: %.2f%%%n",
                    klineData.getS(), direction, percentage);
            // 可以在这里添加您的自定义雷达警报逻辑（如发送通知、自动下单等）
            SocketMessageLdVo ldVo = SocketMessageLdVo.builder().logo("").type("LADAR")
                    .name(klineData.getS().replace("USDT", "/USDT"))
                    .time(time).percentage(percentage).nowTime(System.currentTimeMillis()).build();

            MpCrudTool.insert(ldVo);
            WebSocketLeiDa.sendInfoAll(JSON.toJSONString(ldVo));
        } else {
            System.out.printf("市场平稳。交易对: %s, 幅度: %.2f%%%n",
                    klineData.getS(), percentage);
        }
    }

}
