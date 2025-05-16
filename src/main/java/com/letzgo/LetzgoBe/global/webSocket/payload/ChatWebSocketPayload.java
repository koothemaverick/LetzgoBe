package com.letzgo.LetzgoBe.global.webSocket.payload;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageDto;
import lombok.Data;

@Data
public class ChatWebSocketPayload {
    private String messageType; // "MESSAGE", "READ"
    private Long chatRoomId;

    // message일 때만 존재
    private ChatMessageDto chatMessageDto;

    // read일 때만 존재
    private Long memberId;
    private Long messageId;
}

