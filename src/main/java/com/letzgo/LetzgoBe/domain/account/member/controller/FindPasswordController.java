package com.letzgo.LetzgoBe.domain.account.member.controller;

import com.letzgo.LetzgoBe.domain.account.member.serviceImpl.FindPasswordServiceImpl;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/find-password")
public class FindPasswordController {

    private final FindPasswordServiceImpl findPasswordService;

    // 이메일 인증코드 전송
    @GetMapping("/send-code/email")
    public ApiResponse<String> sendCodeToEmail(@RequestParam("email") String email) {
        findPasswordService.sendEmailVerificationCode(email);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // 이메일 인증코드 인증
    @GetMapping("/verify-code")
    public ApiResponse<String> verifyCodeByEmail(@RequestParam("email") String email, @RequestParam("code") Integer code) {
        String token = findPasswordService.verifyEmailVerificationCode(email, code);
        if(token.equals("INVALID_CODE")) //인증코드 불일치할 경우
            return ApiResponse.of(ReturnCode.INVALID_VERIFICATION_CODE);
        return ApiResponse.of(token);
    }

    // 비밀번호 초기화
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestParam("email") String email,
                                             @RequestParam("token") String token,
                                             @RequestParam("password") String newPassword) {
        boolean result = findPasswordService.resetPassword(email, token, newPassword);
        if (result)
            return ApiResponse.of(ReturnCode.SUCCESS);
        return ApiResponse.of(ReturnCode.INVALID_RESET_TOKEN);
    }
}
