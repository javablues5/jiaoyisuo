package com.ruoyi.websocket;

import com.ruoyi.socket.manager.WebSocketUserManager;
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
    private WebSocketUserManager webSocketUserManager;


    public LDWebSocketSubscriber(URI serverUri, Draft draft) {
        super(serverUri, draft);
        reconnectTimer = new Timer();
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
        System.out.println("雷达5分钟message");
        System.out.println(message);
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
