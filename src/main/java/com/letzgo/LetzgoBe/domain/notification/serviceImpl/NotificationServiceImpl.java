package com.letzgo.LetzgoBe.domain.notification.serviceImpl;

import com.letzgo.LetzgoBe.domain.account.auth.loginUser.LoginUserDto;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.account.member.entity.MemberPage;
import com.letzgo.LetzgoBe.domain.notification.dto.req.NotificationForm;
import com.letzgo.LetzgoBe.domain.notification.dto.res.NotificationDto;
import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import com.letzgo.LetzgoBe.domain.notification.repository.NotificationRepository;
import com.letzgo.LetzgoBe.domain.notification.service.NotificationService;
import com.letzgo.LetzgoBe.global.exception.ReturnCode;
import com.letzgo.LetzgoBe.global.exception.ServiceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    // 알림 생성
    @Override
    @Transactional
    public void createNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    // 알림 목록 조회
    @Override
    @Transactional
    public Page<NotificationDto> getNotifications(Pageable pageable, LoginUserDto loginUser) {
        checkPageSize(pageable.getPageSize());
        Page<Notification> notificationPage = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(loginUser.getId(), pageable);
        return notificationPage.map(this::convertToNotificationDto);
    }

    // 알림 읽음 처리
    @Override
    @Transactional
    public void markAsRead(NotificationForm notificationForm, LoginUserDto loginUser) {
        // 본인 알림인지 확인
        List<Long> ids = notificationForm.getNotificationIdList();
        List<Notification> notifications = notificationRepository.findAllByIdInAndReceiverId(ids, loginUser.getId());
        if (notifications.size() != ids.size()) {
            throw new ServiceException(ReturnCode.NOTIFICATION_NOT_FOUND);
        }
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    // 요청 페이지 수 제한
    private void checkPageSize(int pageSize) {
        int maxPageSize = MemberPage.getMaxPageSize();
        if (pageSize > maxPageSize) {
            throw new ServiceException(ReturnCode.PAGE_REQUEST_FAIL);
        }
    }

    // Notification을 NotificationDto로 변환
    private NotificationDto convertToNotificationDto(Notification notification){
        return new NotificationDto(
                notification.getId(),
                notification.getSenderId(),
                notification.getReceiverId(),
                notification.getObjectId(),
                notification.getContent(),
                notification.getIsRead(),
                notification.getTargetObject()
        );
    }
}
