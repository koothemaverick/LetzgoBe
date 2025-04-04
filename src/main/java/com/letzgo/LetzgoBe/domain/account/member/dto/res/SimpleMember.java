package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleMember {
    private Long userId;
    private String userNickname;
    private String profileImageUrl;
}
