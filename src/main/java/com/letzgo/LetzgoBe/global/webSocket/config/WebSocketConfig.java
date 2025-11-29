package com.letzgo.LetzgoBe.global.webSocket.config;

import com.letzgo.LetzgoBe.global.webSocket.handler.ChatRoomWebSocketHandler;
import com.letzgo.LetzgoBe.global.webSocket.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatRoomWebSocketHandler chatRoomWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOrigins("*");
        registry.addHandler(chatRoomWebSocketHandler, "/ws/chat-room")
                .setAllowedOrigins("*");
    }
}
