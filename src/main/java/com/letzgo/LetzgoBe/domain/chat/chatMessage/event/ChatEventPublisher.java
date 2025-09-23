package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishLastMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("last-message-topic", payload);
    }

    public void publishImageMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("image-message-topic", payload);
    }

    public void publishReadAllMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("read-all-message-topic", payload);
    }

    private void sendMessage(String topic, ChatWebSocketPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize chat message payload", e);
        }
    }
}
