package com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.res;

import com.letzgo.LetzgoBe.domain.account.user.dto.res.ChatRoomUser;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ChatRoomList {
    private Long ROOM_MEMBER_LIMIT;
    private Long id;
    private User user;
    private List<ChatRoomUser> joinUserList;

    public static ChatRoomList from(ChatRoom chatRoom) {
        return ChatRoomList.builder()
                .ROOM_MEMBER_LIMIT(ChatRoom.ROOM_MEMBER_LIMIT)
                .id(chatRoom.getId())
                .user(chatRoom.getUser())
                .joinUserList(chatRoom.getJoinUserList().stream().map(ChatRoomUser::from).collect(Collectors.toList()))
                .build();
    }
}
