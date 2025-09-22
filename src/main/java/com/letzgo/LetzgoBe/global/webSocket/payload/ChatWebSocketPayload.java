package com.letzgo.LetzgoBe.global.webSocket.payload;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageResponse;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ChatWebSocketPayload {
    @Enumerated(EnumType.STRING)
    @Column(length = 7)
    private MessageType messageType;
    public enum MessageType {
        MESSAGE,
        READ,
        READALL,
        PING
    }

    private Long memberId;

    private Long chatRoomId;

    // MESSAGE일 때만 존재
    private ChatMessageResponse chatMessageResponse;
    private String content;
    private LocalDateTime lastMessageCreatedAt;

    // READ일 때만 존재
    private Long messageId;

    // READALL일 때만 존재
    private List<Long> readMessageIdList;
}
