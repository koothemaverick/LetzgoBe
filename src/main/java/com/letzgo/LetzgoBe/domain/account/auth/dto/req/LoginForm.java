package com.letzgo.LetzgoBe.domain.account.auth.dto.req;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class LoginForm {
    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String password;
}
