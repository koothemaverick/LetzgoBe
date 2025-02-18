package com.letzgo.LetzgoBe.domain.account.user.dto.res;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomUser {
    private Long id;
    private String nickName;

    public static ChatRoomUser from(User user) {
        return ChatRoomUser.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .build();
    }
}
