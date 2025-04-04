package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DetailMemberDto {
    private Long id;
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private Member.Gender gender;  // 성별
    private LocalDate birthday;
    private String profileImageUrl;
    private Long followMemberCount;
    private Long followedMemberCount;
    private List<SimpleMember> followList;
    private List<SimpleMember> followedList;
    private List<SimpleMember> followReqList;
    private List<SimpleMember> followRecList;
}
