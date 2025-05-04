package com.letzgo.LetzgoBe.domain.chat.chatMessage.event.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.event.ChatReadAllEvent;
import com.letzgo.LetzgoBe.global.webSocket.ChatWebSocketHandler;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatReadAllPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatReadAllEventListener {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleChatReadAllEvent(ChatReadAllEvent event) {
        ChatReadAllPayload payload = new ChatReadAllPayload();
        payload.setChatRoomId(event.getChatRoomId());
        payload.setMemberId(event.getMemberId());
        payload.setLastReadMessageId(event.getLastReadMessageId());

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            chatWebSocketHandler.broadcastToRoom(event.getChatRoomId(), jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize ChatReadAllPayload", e);
        }
    }
}
