package com.letzgo.LetzgoBe.domain.chat.chatMessage.event.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.event.ChatMessageCreatedEvent;
import com.letzgo.LetzgoBe.global.webSocket.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageCreatedEventListener {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleChatMessageCreated(ChatMessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event.getChatMessageDto());
            chatWebSocketHandler.broadcastToRoom(event.getChatRoomId(), payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize chat message", e);
        }
    }
}
