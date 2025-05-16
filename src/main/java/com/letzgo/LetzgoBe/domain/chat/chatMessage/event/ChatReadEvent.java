package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatReadEvent {
    private Long memberId;
    private Long messageId;
    private Long chatRoomId;
}
