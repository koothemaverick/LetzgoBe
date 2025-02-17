package com.letzgo.LetzgoBe.domain.account.user.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.user.dto.req.UserForm;
import com.letzgo.LetzgoBe.domain.account.user.dto.res.UserInfo;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.domain.account.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("letzgo/rest-api/v1/user")
@RequiredArgsConstructor
public class ApiV1UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserForm userForm) {
        userService.signup(userForm);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 회원정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getUserInfo(@RequestHeader("Authorization") String token, @LoginUser User loginUser) {
        return ResponseEntity.ok(userService.getUserInfo(token, loginUser));
    }

    // 회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateUserInfo(@RequestBody UserForm userForm, @RequestHeader("Authorization") String token, @LoginUser User loginUser) {
        userService.updateUser(userForm, token, loginUser);
        return ResponseEntity.ok("회원정보 수정 성공");
    }

    // 회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token, @LoginUser User loginUser) {
        userService.deleteUser(token, loginUser);
        return ResponseEntity.ok("회원탈퇴 성공");
    }

    // 해당 유저 팔로우 하기

    // 해당 유저 팔로우 취소하기

    // 팔로우 목록 가져오기

    // 팔로우 목록에서 검색하기

    // 팔로우 목록 수정하기

    // 팔로워 목록 가져오기

    // 팔로워 목록에서 검색하기

    // 팔로워 목록 수정하기

    // 팔로워 목록에서 해당 유저 삭제하기
}
