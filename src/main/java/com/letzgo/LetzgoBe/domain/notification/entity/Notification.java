package com.letzgo.LetzgoBe.domain.notification.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Notification extends BaseEntity {
    private Long senderId;
    private Long receiverId;
    private Long objectId;
    private String title;
    private String content;

    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TargetObject targetObject;
    public enum TargetObject {
        Comment, Follow, Post
    }
}
