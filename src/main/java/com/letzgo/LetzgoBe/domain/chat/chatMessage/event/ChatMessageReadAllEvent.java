package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageReadAllEvent {
    private final ChatWebSocketPayload.MessageType messageType = ChatWebSocketPayload.MessageType.READALL;
    private final Long memberId;
    private final Long chatRoomId;
    private final Long lastReadMessageId;
}
