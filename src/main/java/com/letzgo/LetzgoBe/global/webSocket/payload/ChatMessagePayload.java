package com.letzgo.LetzgoBe.global.webSocket.payload;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.req.ChatMessageForm;
import lombok.Data;

@Data
public class ChatMessagePayload {
    private Long chatRoomId;
    private ChatMessageForm chatMessageForm; // 메시지 내용 (MESSAGE일 때 사용)
    private Long memberId;                   // 누가 보냈거나 읽었는지
    private String messageType;              // "MESSAGE" or "READ"
    private Long messageId;                  // READ일 때 읽은 메시지 ID
}
