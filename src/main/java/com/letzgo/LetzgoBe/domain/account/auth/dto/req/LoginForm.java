package com.letzgo.LetzgoBe.domain.account.auth.dto.req;

import lombok.Data;

@Data
public class LoginForm {
    private String email;
    private String password;
}
