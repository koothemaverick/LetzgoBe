package com.letzgo.LetzgoBe.domain.notification.controller;

import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUser;
import com.letzgo.LetzgoBe.domain.account.auth.currentUser.CurrentUserDto;
import com.letzgo.LetzgoBe.domain.notification.dto.req.NotificationRequest;
import com.letzgo.LetzgoBe.domain.notification.dto.res.NotificationResponse;
import com.letzgo.LetzgoBe.domain.notification.entity.NotificationPage;
import com.letzgo.LetzgoBe.domain.notification.service.NotificationService;
import com.letzgo.LetzgoBe.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest-api/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 API")
public class ApiV1NotificationController {
    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "알림 목록을 조회합니다.")
    public ApiResponse<List<NotificationResponse>> getNotifications(@ModelAttribute NotificationPage request, @CurrentUser CurrentUserDto currentUser) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ApiResponse.success(notificationService.getNotifications(pageable, currentUser));
    }

    // 알림 읽음 처리
    @PatchMapping
    @Operation(summary = "알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    public ApiResponse<Void> markAsRead(@RequestBody @Valid NotificationRequest notificationRequest,
                                          @CurrentUser CurrentUserDto currentUser) {
        notificationService.markAsRead(notificationRequest, currentUser);
        return ApiResponse.success();
    }
}
