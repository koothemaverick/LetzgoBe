package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;
    private String name;
    private String nickName;
    private String profileImageUrl;
    private Long followMemberCount;
    private Long followedMemberCount;
}
