package com.letzgo.LetzgoBe.domain.notification.controller;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUser;
import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.notification.dto.req.NotificationRequest;
import com.letzgo.LetzgoBe.domain.notification.dto.res.NotificationResponse;
import com.letzgo.LetzgoBe.domain.notification.entity.NotificationPage;
import com.letzgo.LetzgoBe.domain.notification.service.NotificationService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/notification")
@RequiredArgsConstructor
public class ApiV1NotificationController {
    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(@ModelAttribute NotificationPage request, @LoginUser LoginUserDto loginUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(notificationService.getNotifications(pageable, loginUser));
    }

    // 알림 읽음 처리
    @PatchMapping
    public ApiResponse<Void> markAsRead(@RequestBody @Valid NotificationRequest notificationRequest,
                                          @LoginUser LoginUserDto loginUser) {
        notificationService.markAsRead(notificationRequest, loginUser);
        return ApiResponse.success();
    }
}
