package com.letzgo.LetzgoBe.domain.account.user.dto.res;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserInfo {
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private User.Gender gender;  // 성별
    private LocalDate birthday;

    public static UserInfo from(LoginUserDto loginUser) {
        return UserInfo.builder()
                .name(loginUser.getName())
                .nickName(loginUser.getNickName())
                .phone(loginUser.getPhone())
                .email(loginUser.getEmail())
                .gender(loginUser.getGender())
                .birthday(loginUser.getBirthday())
                .build();
    }
}
