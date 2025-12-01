package com.letzgo.LetzgoBe.domain.account.member.converter;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.DetailMemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.dto.res.MemberResponse;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = SimpleMemberMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberConverter {
    // ---------- Member -> MemberResponse ----------
    @Mapping(target = "followMemberCount", expression = "java(source.getFollowList() != null ? (long) source.getFollowList().size() : 0L)")
    @Mapping(target = "followedMemberCount", expression = "java(source.getFollowedList() != null ? (long) source.getFollowedList().size() : 0L)")
    MemberResponse toMemberResponse(Member source);

    // ---------- Member -> DetailMemberResponse ----------
    @Mapping(target = "followMemberCount", expression = "java(source.getFollowList() != null ? (long) source.getFollowList().size() : 0L)")
    @Mapping(target = "followedMemberCount", expression = "java(source.getFollowedList() != null ? (long) source.getFollowedList().size() : 0L)")
    @Mapping(target = "followList", source = "followList", qualifiedByName = "toFollowingMembers")
    @Mapping(target = "followedList", source = "followedList", qualifiedByName = "toFollowerMembers")
    @Mapping(target = "followReqList", source = "followReqList", qualifiedByName = "toSimpleMembersFromFollowReqs")
    @Mapping(target = "followRecList", source = "followRecList", qualifiedByName = "toSimpleMembersFromFollowRecs")
    DetailMemberResponse toDetailMemberResponse(Member source);

    // ---------- LoginUserDto <-> Member ----------
    Member toMember(CurrentUserDto source);
    CurrentUserDto toCurrentUserDto(Member source);
}
