package com.letzgo.LetzgoBe.domain.notification.service;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.notification.dto.req.NotificationForm;
import com.letzgo.LetzgoBe.domain.notification.dto.res.NotificationDto;
import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    // 알림 생성
    void createNotification(Notification notification);

    // 알림 목록 조회
    Page<NotificationDto> getNotifications(Pageable pageable, LoginUserDto loginUser);

    // 알림 읽음 처리
    void markAsRead(NotificationForm notificationForm, LoginUserDto loginUser);
}
