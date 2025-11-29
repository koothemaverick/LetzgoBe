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

    // 채팅 메시지 생성 이벤트
    public void publishChatMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("chat-message-topic", payload);
    }

    // 단일 메시지 읽음 처리 이벤트
    public void publishReadMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("read-message-topic", payload);
    }

    // 채팅방 리스트용 마지막 메시지 이벤트
    public void publishLastMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("last-message-topic", payload);
    }

    // 이미지 메시지 이벤트
    public void publishImageMessageEvent(ChatWebSocketPayload payload) {
        sendMessage("image-message-topic", payload);
    }

    // 전체 읽음 처리 이벤트
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
