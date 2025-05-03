package com.letzgo.LetzgoBe.domain.chat.chatMessage.eventListener;

import com.letzgo.LetzgoBe.domain.chat.chatMessage.dto.res.ChatMessageDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageCreatedEvent {
    private final Long chatRoomId;
    private final ChatMessageDto chatMessageDto;
}
