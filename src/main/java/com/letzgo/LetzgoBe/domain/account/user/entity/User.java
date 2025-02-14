package com.letzgo.LetzgoBe.domain.account.user.entity;

import com.letzgo.LetzgoBe.domain.dm.chatRoom.converter.StringListConverter;
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
    @Column(length = 20)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(length = 1)
    private Gender gender;  // 성별
    @Getter
    public enum Gender {
        M, W;
    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('NORMAL', 'DELETED', 'BANNED') DEFAULT 'NORMAL'")
    private MemberState state;  // 회원 상태
    @Getter
    public enum MemberState {
        NORMAL,  // 정상
        BANNED  // 정지
    }
    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> joinRoomIdList;  // 참여하고 있는 채팅방ID 리스트
    private LocalDate birthday;
}
