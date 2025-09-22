package com.letzgo.LetzgoBe.domain.fcm.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.fcm.dto.req.FcmTokenRequest;
import com.letzgo.LetzgoBe.domain.fcm.service.FcmTokenService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest-api/v1/fcm")
@RequiredArgsConstructor
public class ApiV1FcmController {
    private final FcmTokenService fcmTokenService;

    // FCM Token 저장
    @PostMapping
    public ApiResponse<String> saveFcmToken(@RequestBody @Valid FcmTokenRequest fcmTokenRequest, @LoginUser LoginUserDto loginUser) {
        fcmTokenService.saveFcmToken(loginUser.getId(), fcmTokenRequest.getFcmToken());
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    // FCM Token 삭제
    @DeleteMapping
    public ApiResponse<String> deleteFcmToken(@LoginUser LoginUserDto loginUser) {
        fcmTokenService.deleteFcmToken(loginUser.getId());
        return ApiResponse.of(ReturnCode.SUCCESS);
    }
}
