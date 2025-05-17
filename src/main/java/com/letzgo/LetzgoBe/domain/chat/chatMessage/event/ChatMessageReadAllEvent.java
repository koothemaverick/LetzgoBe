package com.letzgo.LetzgoBe.domain.chat.chatMessage.event;

import com.letzgo.LetzgoBe.global.webSocket.payload.ChatWebSocketPayload;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ChatMessageReadAllEvent {
    private final ChatWebSocketPayload.MessageType messageType = ChatWebSocketPayload.MessageType.READALL;
    private final Long chatRoomId;
    private final List<Long> readMessageIdList;
}
