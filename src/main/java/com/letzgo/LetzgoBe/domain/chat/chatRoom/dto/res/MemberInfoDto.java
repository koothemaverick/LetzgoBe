package com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberInfoDto {
    private Long userId;
    private String userNickname;
    private String profileImageUrl;
}
