package com.letzgo.LetzgoBe.domain.dm.chatRoom.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ChatRoom extends BaseEntity {
    public static final Long ROOM_MEMBER_LIMIT = 100L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany
    @JoinColumn(name = "user_id")
    @JsonBackReference // 자식 역할
    private List<User> joinUserList;
}
