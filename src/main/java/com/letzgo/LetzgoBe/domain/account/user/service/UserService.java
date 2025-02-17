package com.letzgo.LetzgoBe.domain.account.user.service;

import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.dto.res.UserInfo;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;

public interface UserService {
    // 회원가입
    void signup(UserForm userForm);

    // 회원정보 조회
    UserInfo getUserInfo(String token, User loginUser);

    // 회원정보 수정
    void updateUser(UserForm userForm, String token, User loginUser);

    // 회원탈퇴
    void deleteUser(String token, User loginUser);
}
