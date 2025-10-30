package com.ruoyi.websocket.sevice;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.bussiness.domain.TBotKlineModelInfo;
import com.ruoyi.bussiness.domain.TSecondCoinConfig;
import com.ruoyi.bussiness.klineDto.KlineData;
import com.ruoyi.bussiness.mapper.TBotKlineModelInfoMapper;
import com.ruoyi.bussiness.service.ITSecondCoinConfigService;
import com.ruoyi.common.crud.MpCrudTool;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.socket.config.KLoader;
import com.ruoyi.socket.dto.SocketMessageLdVo;
import com.ruoyi.socket.socketserver.WebSocketLeiDa;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

@Service
public class LDSevice {


    // 目标阈值：5%
    private static final BigDecimal THRESHOLD = new BigDecimal("0.0003");

    @Resource
    private TBotKlineModelInfoMapper tBotKlineModelInfoMapper;
    @Resource
    private ITSecondCoinConfigService itSecondCoinConfigService;

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
        aVoid(klineData, klineData.getS().replace("usdt", ""));

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
                    .name(klineData.getS().replace("usdt", "/usdt"))
                    .time(time).percentage(percentage).nowTime(System.currentTimeMillis()).build();

            TSecondCoinConfig symbol = itSecondCoinConfigService.getOne(new LambdaQueryWrapper<TSecondCoinConfig>().eq(TSecondCoinConfig::getSymbol, klineData.getS()));
            if (Objects.nonNull(symbol)) {
                ldVo.setLogo(symbol.getLogo());
            }
            MpCrudTool.insert(ldVo);
            WebSocketLeiDa.sendInfoAll(JSON.toJSONString(ldVo));
        } else {
            System.out.printf("市场平稳。交易对: %s, 幅度: %.2f%%%n",
                    klineData.getS(), percentage);
        }
    }

    // 控线波动
    public void aVoid(KlineData klineData, String coin){
        if(KLoader.BOT_MAP.containsKey("bot-"+coin)){
            Map<String, Object> cacheMap = KLoader.BOT_MAP.get("bot-"+coin);

            TBotKlineModelInfo tBotKlineModelInfo = new TBotKlineModelInfo();
            tBotKlineModelInfo.setModelId((Long)cacheMap.get("id"));
            tBotKlineModelInfo.setDateTime(DateUtils.getTimestamp_MINUTES());
            TBotKlineModelInfo bot =tBotKlineModelInfoMapper.selectOne(new QueryWrapper<>(tBotKlineModelInfo));
            if(bot!=null&&!bot.getY().equals("0")){
                //获取当时对比价格
                BigDecimal currentlyPrice = (BigDecimal) cacheMap.get("currentlyPrice");
                BigDecimal open = currentlyPrice.multiply(bot.getOpen().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                klineData.setO(currentlyPrice.add(open));
                BigDecimal close = currentlyPrice.multiply(bot.getClose().divide(new BigDecimal("100"), 8, RoundingMode.HALF_UP));
                klineData.setC(currentlyPrice.add(close));
            }
        }
    }

}
