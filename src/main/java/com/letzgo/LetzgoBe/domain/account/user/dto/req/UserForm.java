package com.letzgo.LetzgoBe.domain.account.user.dto.req;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserForm {
    private String name;
    private String nickName;
    private String phone;
    private String email;
    private String password;
    private User.Gender gender;
    private LocalDate birthday;
}
