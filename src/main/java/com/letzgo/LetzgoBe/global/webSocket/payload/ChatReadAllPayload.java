package com.letzgo.LetzgoBe.global.webSocket.payload;

import lombok.Data;

@Data
public class ChatReadAllPayload {
    private String messageType = "READ_ALL";
    private Long chatRoomId;
    private Long memberId;
    private Long lastReadMessageId;
}
