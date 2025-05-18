package com.letzgo.LetzgoBe.domain.notification.dto.res;

import com.letzgo.LetzgoBe.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long senderId;
    private Long objectId;
    private String content;
    private Boolean isRead;
    private Notification.TargetObject targetObject;
}
