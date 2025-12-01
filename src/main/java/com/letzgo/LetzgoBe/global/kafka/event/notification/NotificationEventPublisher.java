package com.letzgo.LetzgoBe.global.kafka.event.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishCommentNotification(Notification notification) {
        sendMessage("comment-topic", notification);
    }

    public void publishFollowNotification(Notification notification) {
        sendMessage("follow-topic", notification);
    }

    public void publishPostNotification(Notification notification) {
        sendMessage("post-topic", notification);
    }

    private void sendMessage(String topic, Notification notification) {
        try {
            String json = objectMapper.writeValueAsString(notification);
            kafkaTemplate.send(topic, json);
            log.info("Published notification event. topic={}, receiverId={}", topic, notification.getReceiverId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize Notification for topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to serialize notification payload", e);
        }
    }
}
