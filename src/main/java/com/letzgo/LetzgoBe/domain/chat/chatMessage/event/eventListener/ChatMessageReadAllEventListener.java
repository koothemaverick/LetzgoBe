package com.letzgo.LetzgoBe.domain.chat.chatMessage.event.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.event.ChatMessageReadAllEvent;
import com.letzgo.LetzgoBe.global.webSocket.ChatWebSocketHandler;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageReadAllEventListener {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleChatMessageReadAll(ChatMessageReadAllEvent event) {
        try {
            // ChatWebSocketPayload 객체 생성 및 필요한 필드만 설정
            ChatWebSocketPayload payload = new ChatWebSocketPayload();
            payload.setMessageType(ChatWebSocketPayload.MessageType.READALL);
            payload.setMemberId(event.getMemberId());
            payload.setChatRoomId(event.getChatRoomId());
            payload.setLastReadMessageId(event.getLastReadMessageId());

            String serializedPayload = objectMapper.writeValueAsString(payload);
            chatWebSocketHandler.broadcastToRoom(event.getChatRoomId(), serializedPayload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize read all payload", e);
        }
    }
}
