package com.letzgo.LetzgoBe.domain.chat.chatMessage.repository;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.entity.MessageContent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageContentRepository extends MongoRepository<MessageContent, String> {
    // MongoDB에서 메시지 내용 불러오기
    List<MessageContent> findByIdIn(List<String> messageIds);
}
