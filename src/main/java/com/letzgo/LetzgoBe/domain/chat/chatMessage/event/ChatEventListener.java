package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageResponse;
import com.letzgo.LetzgoBe.domain.chat.chatMessage.service.ChatMessageService;
import com.letzgo.LetzgoBe.global.webSocket.handler.ChatRoomWebSocketHandler;
import com.letzgo.LetzgoBe.global.webSocket.handler.ChatWebSocketHandler;
import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventListener {
    private final ObjectMapper objectMapper;
    private final ChatRoomWebSocketHandler chatRoomWebSocketHandler;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ChatMessageService chatMessageService;
    private final Map<String, ChatTopicMeta> topicMetaMap = Map.of(
            "chat-message-topic", new ChatTopicMeta("chat-message-topic-dlt", this::handleChatMessageEvent),
            "read-message-topic", new ChatTopicMeta("read-message-topic-dlt", this::handleReadMessageEvent),
            "last-message-topic", new ChatTopicMeta("last-message-topic-dlt", this::handleLastMessage),
            "image-message-topic", new ChatTopicMeta("image-message-topic-dlt", this::handleImageMessage),
            "read-all-message-topic", new ChatTopicMeta("read-all-message-topic-dlt", this::handleReadAllMessage)
    );

    @KafkaListener(topics = {
            "chat-message-topic",
            "read-message-topic",
            "last-message-topic",
            "image-message-topic",
            "read-all-message-topic"
    }, groupId = "chat-group")
    public void consume(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String message = record.value();
        ChatTopicMeta meta = topicMetaMap.get(topic);
        if (meta == null) {
            log.warn("Unknown topic: {}", topic);
            return;
        }
        handleChatMessage(message, meta.handler(), meta.dltTopic());
    }

    private void handleChatMessage(String message, ChatMessageHandler handler, String dltTopic) {
        int maxRetries = 3;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                ChatWebSocketPayload payload = objectMapper.readValue(message, ChatWebSocketPayload.class);
                handler.handle(payload, message);
                return;
            } catch (Exception e) {
                attempt++;
                log.error("WebSocket message handling failed. Attempt {}: {}", attempt, e.getMessage());
            }
        }
        log.error("Message permanently failed. Sending to DLT: {}", dltTopic);
        kafkaTemplate.send(dltTopic, message);
    }

    private void handleLastMessage(ChatWebSocketPayload payload, String rawJson) {
        log.info("Consumed last-message-topic: chatRoomId={}, type={}", payload.getChatRoomId(), payload.getMessageType());
        chatRoomWebSocketHandler.sendLatestMessageToAllMembers(payload.getChatRoomId(), payload);
    }

    private void handleImageMessage(ChatWebSocketPayload payload, String rawJson) {
        log.info("Consumed image-message-topic: chatRoomId={}, type={}", payload.getChatRoomId(), payload.getMessageType());
        chatWebSocketHandler.broadcastToRoom(payload.getChatRoomId(), rawJson);
    }

    private void handleReadAllMessage(ChatWebSocketPayload payload, String rawJson) {
        log.info("Consumed read-all-message-topic: chatRoomId={}, readIds={}", payload.getChatRoomId(), payload.getReadMessageIdList());
        chatWebSocketHandler.broadcastToRoom(payload.getChatRoomId(), rawJson);
    }

    private void handleChatMessageEvent(ChatWebSocketPayload payload, String rawJson) {
        log.info("Consumed chat-message-topic: chatRoomId={}, type={}",
                payload.getChatRoomId(), payload.getMessageType());

        ChatMessageResponse savedChatMessageResponse = chatMessageService.writeChatMessage(
                payload.getChatRoomId(),
                payload.getChatMessageResponse().getContent(),
                payload.getChatMessageResponse().getMemberId()
        );
        payload.setChatMessageResponse(savedChatMessageResponse);

        try {
            String updatedJson = objectMapper.writeValueAsString(payload);
            chatWebSocketHandler.broadcastToRoom(payload.getChatRoomId(), updatedJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize chat message payload", e);
        }
    }

    private void handleReadMessageEvent(ChatWebSocketPayload payload, String rawJson) {
        log.info("Consumed read-message-topic: chatRoomId={}, messageId={}, memberId={}",
                payload.getChatRoomId(), payload.getMessageId(), payload.getMemberId());

        chatMessageService.readChatMessage(payload.getMessageId(), payload.getMemberId());
        chatWebSocketHandler.broadcastToRoom(payload.getChatRoomId(), rawJson);
    }

    @FunctionalInterface
    private interface ChatMessageHandler {
        void handle(ChatWebSocketPayload payload, String rawJson);
    }

    private record ChatTopicMeta(String dltTopic, ChatMessageHandler handler) {}
}
