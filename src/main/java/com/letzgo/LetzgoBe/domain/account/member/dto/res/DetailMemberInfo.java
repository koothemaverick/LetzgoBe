package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberFollow;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberFollowReq;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.dto.res.MemberInfoDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DetailMemberInfo {
    private Long id;
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private Member.Gender gender;  // 성별
    private LocalDate birthday;
    private String profileImgUrl;
    private Long followMemberCount;
    private Long followedMemberCount;
    private List<MemberInfoDto> followList;
    private List<MemberInfoDto> followedList;
    private List<MemberInfoDto> followReqList;
    private List<MemberInfoDto> followRecList;
}
