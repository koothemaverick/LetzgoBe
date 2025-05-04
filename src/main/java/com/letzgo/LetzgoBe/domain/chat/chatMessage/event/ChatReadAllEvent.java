package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatReadAllEvent {
    private Long chatRoomId;
    private Long memberId;
    private Long lastReadMessageId;
}
