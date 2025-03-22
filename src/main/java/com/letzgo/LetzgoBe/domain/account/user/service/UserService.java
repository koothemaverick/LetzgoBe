package com.letzgo.LetzgoBe.domain.account.user.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.dto.res.UserInfo;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;

public interface UserService {
    // 회원가입
    User signup(UserForm userForm);

    // 회원정보 조회
    UserInfo getUserInfo(LoginUserDto loginUser);

    // 회원정보 수정
    void updateUser(UserForm userForm, LoginUserDto loginUser);

    // 회원탈퇴
    void deleteUser(LoginUserDto loginUser);
}
