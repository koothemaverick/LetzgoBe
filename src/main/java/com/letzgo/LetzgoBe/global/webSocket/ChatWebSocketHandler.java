package com.letzgo.LetzgoBe.global.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.eventListener.ChatMessageCreatedEvent;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final Map<Long, List<WebSocketSession>> chatRoomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessagePayload payload = objectMapper.readValue(message.getPayload(), ChatMessagePayload.class);
        if ("MESSAGE".equalsIgnoreCase(payload.getMessageType())) {
            // 채팅 메시지 생성
            chatMessageService.writeChatMessage(payload.getChatRoomId(), payload.getChatMessageForm(), payload.getMemberId());
            // 모두에게 메시지 브로드캐스트
            broadcastToRoom(payload.getChatRoomId(), message.getPayload());
        } else if ("READ".equalsIgnoreCase(payload.getMessageType())) {
            // 메시지 읽음 처리
            chatMessageService.readChatMessage(payload.getMessageId(), payload.getMemberId());
            // 읽음 상태 브로드캐스트
            broadcastToRoom(payload.getChatRoomId(), message.getPayload());
        } else {
            log.warn("Unknown messageType: " + payload.getMessageType());
        }
    }

    @EventListener
    public void handleChatMessageCreated(ChatMessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event.getChatMessageDto());
            broadcastToRoom(event.getChatRoomId(), payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize chat message", e);
        }
    }

    public void broadcastToRoom(Long chatRoomId, String payload) {
        List<WebSocketSession> sessions = chatRoomSessions.getOrDefault(chatRoomId, new ArrayList<>());
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
                log.error("Failed to send message", e);
            }
        }
    }
}
