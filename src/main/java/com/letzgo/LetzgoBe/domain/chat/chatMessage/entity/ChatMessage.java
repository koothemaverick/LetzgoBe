package com.letzgo.LetzgoBe.domain.chat.chatMessage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
import com.letzgo.LetzgoBe.domain.chat.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @JsonIgnore
    private ChatRoom chatRoom;

    @Type(JsonType.class) // Hibernate JSON 타입 적용
    @Column(columnDefinition = "jsonb") // PostgreSQL의 jsonb 타입 사용
    private List<String> imageUrls;

    private Long readCount;
}
