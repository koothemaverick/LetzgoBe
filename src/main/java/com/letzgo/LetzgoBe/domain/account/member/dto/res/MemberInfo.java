package com.letzgo.LetzgoBe.domain.account.member.dto.res;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MemberInfo {
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private Member.Gender gender;  // 성별
    private LocalDate birthday;

    public static MemberInfo from(LoginUserDto loginUser) {
        return MemberInfo.builder()
                .name(loginUser.getName())
                .nickName(loginUser.getNickname())
                .phone(loginUser.getPhone())
                .email(loginUser.getEmail())
                .gender(loginUser.getGender())
                .birthday(loginUser.getBirthday())
                .build();
    }
}
