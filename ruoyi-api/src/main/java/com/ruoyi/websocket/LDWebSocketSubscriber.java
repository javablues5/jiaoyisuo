package com.ruoyi.websocket;

import com.alibaba.fastjson.JSON;
import com.ruoyi.bussiness.klineDto.KlineData;
import com.ruoyi.websocket.sevice.LDSevice;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.*;

@Slf4j
public class LDWebSocketSubscriber extends WebSocketClient {
    private static final long RECONNECT_INTERVAL = 5000; // 重连间隔，单位：毫秒

    private Timer reconnectTimer;
    private LDSevice ldSevice;
    private String time;

    public LDWebSocketSubscriber(URI serverUri, Draft draft, String time, LDSevice ldSevice) {
        super(serverUri, draft);
        reconnectTimer = new Timer();
        this.time = time;
        this.ldSevice = ldSevice;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
       log.info("WebSocket connection opened.");
    }

    @Override
    public void onMessage(String message) {
        // 处理接收到的消息
        // 如果收到服务器发送的ping帧，回复pong帧
        if (message.equals("Ping")) {
            send("Pong");
        }
        try {
            //System.out.println("雷达5分钟message");
            //System.out.println(message);
            KlineData k = JSON.parseObject(message).getObject("k", KlineData.class);
            //System.out.println(k.toString());
            ldSevice.checkPriceChange(k,time);
        } catch (Exception e) {
            log.error("雷达处理消息时错误：",e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket connection closed. Code: " + code + ", Reason: " + reason);
        if(-1 != code){
            reconnectWebSocket();
        }
    }

    @Override
    public void onError(Exception e) {
        System.err.println(e);
        System.err.println("WebSocket error: " + e.getMessage());
        reconnectWebSocket();
    }

    private void reconnectWebSocket() {
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                reconnect();
                System.out.println("Attempting to reconnect to WebSocket...");
            }
        }, RECONNECT_INTERVAL);
    }

}
