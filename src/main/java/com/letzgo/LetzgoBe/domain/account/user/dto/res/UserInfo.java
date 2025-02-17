package com.letzgo.LetzgoBe.domain.account.user.dto.res;

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

    public static UserInfo from(User user) {
        return UserInfo.builder()
                .name(user.getName())
                .nickName(user.getNickName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .build();
    }
}
