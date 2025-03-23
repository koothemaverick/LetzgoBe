package com.letzgo.LetzgoBe.domain.account.member.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DynamicInsert
public class MemberFollow extends BaseEntity {
    // 팔로우 하는 멤버
    @ManyToOne
    @JoinColumn(name = "follow_id")
    private Member follow;

    // 팔로우 당하는 멤버
    @ManyToOne
    @JoinColumn(name = "followed_id")
    private Member followed;
}
