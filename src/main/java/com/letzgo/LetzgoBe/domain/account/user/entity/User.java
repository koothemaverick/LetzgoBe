package com.letzgo.LetzgoBe.domain.account.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.letzgo.LetzgoBe.domain.dm.chatRoom.entity.ChatRoom;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DynamicInsert
public class User extends BaseEntity {
    @Column(length = 10)
    private String name;

    @Column(length = 20)
    private String nickName;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;
    
    @Column(length = 1000)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;  // 성별
    @Getter
    public enum Gender {
        MALE, FEMALE;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('NORMAL', 'DELETED', 'BANNED') DEFAULT 'NORMAL'")
    private State state;  // 회원 상태
    @Getter
    public enum State {
        NORMAL,  // 정상
        BANNED  // 정지
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ROLE_USER', 'ROLE_ADMIN') DEFAULT 'ROLE_USER'")
    private MemberRole role = MemberRole.ROLE_USER;  // 권한 (관리자, 사용자)
    @Getter
    public enum MemberRole {
        ROLE_USER, ROLE_ADMIN
    }

    @OneToMany
    @JoinColumn(name = "chatroom_id")
    @JsonManagedReference // 부모 역할
    private List<ChatRoom> joinChatRoomList;  // 참여하고 있는 채팅방 리스트

    @OneToMany(mappedBy = "follower")
    private List<UserFollow> followUserList;

    @OneToMany(mappedBy = "followee")
    private List<UserFollow> followerUserList;

    private LocalDate birthday;
}
