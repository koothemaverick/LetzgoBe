package com.letzgo.LetzgoBe.global.webSocket.payload;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.ChatMessageDto;
import lombok.Data;

@Data
public class ChatMessagePayload {
    private Long chatRoomId;
    private String messageType = "MESSAGE";
    private ChatMessageDto chatMessageDto;
}
