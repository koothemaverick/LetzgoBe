package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberInfo {
    private Long id;
    private String name;
    private String nickName;
    private String profileImgUrl;
    private Long followMemberCount;
    private Long followedMemberCount;
}
