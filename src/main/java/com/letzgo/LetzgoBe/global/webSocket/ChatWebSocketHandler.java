package com.letzgo.LetzgoBe.global.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private final Map<WebSocketSession, Long> sessionRoomMap = new ConcurrentHashMap<>(); // 세션 -> 방 매핑

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery(); // e.g., "chatRoomId=123"
        if (query != null && query.startsWith("chatRoomId=")) {
            Long chatRoomId = Long.parseLong(query.split("=")[1]);

            chatRoomSessions.computeIfAbsent(chatRoomId, k -> Collections.synchronizedList(new ArrayList<>())).add(session);
            sessionRoomMap.put(session, chatRoomId);

            log.info("WebSocket connected: {} for chatRoomId: {}", session.getId(), chatRoomId);
        } else {
            log.warn("WebSocket connected without chatRoomId: {}", session.getId());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatWebSocketPayload payload = objectMapper.readValue(message.getPayload(), ChatWebSocketPayload.class);
        if ("MESSAGE".equalsIgnoreCase(payload.getMessageType())) {
            // 채팅 메시지 생성
            chatMessageService.writeChatMessage(payload.getChatRoomId(), payload.getChatMessageDto().getContent(), payload.getChatMessageDto().getMemberId());
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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long chatRoomId = sessionRoomMap.remove(session);
        if (chatRoomId != null) {
            List<WebSocketSession> sessions = chatRoomSessions.get(chatRoomId);
            if (sessions != null) {
                sessions.remove(session);
            }
            log.info("WebSocket disconnected: {} from chatRoomId: {}", session.getId(), chatRoomId);
        } else {
            log.info("WebSocket disconnected without chatRoomId: {}", session.getId());
        }
    }
}
