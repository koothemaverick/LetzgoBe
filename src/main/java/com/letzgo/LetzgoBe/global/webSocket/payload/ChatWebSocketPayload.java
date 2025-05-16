package com.letzgo.LetzgoBe.global.webSocket.payload;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageDto;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ChatWebSocketPayload {
    @Enumerated(EnumType.STRING)
    @Column(length = 7)
    private MessageType messageType;
    public enum MessageType {
        MESSAGE,
        READ,
        READALL
    }

    private Long memberId;

    private Long chatRoomId;

    // MESSAGE일 때만 존재
    private ChatMessageDto chatMessageDto;

    // READ일 때만 존재
    private Long messageId;

    // READALL일 때만 존재
    private Long lastReadMessageId;
}
