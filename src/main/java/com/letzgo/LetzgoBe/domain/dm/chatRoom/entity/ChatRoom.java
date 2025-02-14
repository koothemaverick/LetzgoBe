package com.letzgo.LetzgoBe.domain.dm.chatRoom.entity;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.converter.StringDoubleListConverter;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Convert(converter = StringDoubleListConverter.class)
    private List<List<String>> joinMemberIdNickNameList;
    private Long roomMemberLimit;
}
