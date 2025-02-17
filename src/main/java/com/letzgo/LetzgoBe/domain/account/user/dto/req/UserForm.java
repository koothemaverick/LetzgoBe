package com.letzgo.LetzgoBe.domain.account.user.dto.req;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserForm {
    @Column(length = 10)
    private String name;

    @Column(length = 20)
    private String nickName;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private User.Gender gender;  // 성별

    private LocalDate birthday;
}
