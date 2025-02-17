package com.letzgo.LetzgoBe.domain.dm.chatRoom.dto.req;

import com.letzgo.LetzgoBe.domain.account.user.dto.res.ChatRoomUser;
import lombok.Data;

import java.util.List;

@Data
public class ChatRoomForm {
    private List<ChatRoomUser> joinUserList;
}
