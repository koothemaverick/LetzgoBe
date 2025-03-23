package com.letzgo.LetzgoBe.domain.account.auth.loginUser;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberFollow;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LoginUserDto {
    private Long id;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String password;
    private Member.Gender gender;
    private Member.State state;
    private Member.MemberRole role;
    private String profileImageUrl;
    private List<MemberFollow> followList;
    private List<MemberFollow> followedList;
    private LocalDate birthday;
    private LocalDateTime createDate;

    // Member 객체를 LoginUserDto로 변환하는 정적 팩토리 메서드
    public static LoginUserDto ConvertToLoginUserDto(Member member) {
        return LoginUserDto.builder()
                .id(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .phone(member.getPhone())
                .email(member.getEmail())
                .password(member.getPassword())
                .gender(member.getGender())
                .state(member.getState())
                .role(member.getRole())
                .profileImageUrl(member.getProfileImageUrl())
                .followList(member.getFollowList())
                .followedList(member.getFollowedList())
                .birthday(member.getBirthday())
                .createDate(member.getCreateDate())
                .build();
    }

    // LoginUserDto를 Member 엔티티로 변환하는 메서드
    public Member ConvertToMember() {
        return Member.builder()
                .id(this.id)
                .name(this.name)
                .nickname(this.nickname)
                .phone(this.phone)
                .email(this.email)
                .password(this.password)
                .gender(this.gender)
                .state(this.state)
                .role(this.role)
                .profileImageUrl(this.profileImageUrl)
                .followList(this.followList)
                .followedList(this.followedList)
                .birthday(this.birthday)
                .createDate(this.createDate)
                .build();
    }
}
