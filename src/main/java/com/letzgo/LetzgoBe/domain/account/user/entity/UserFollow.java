package com.letzgo.LetzgoBe.domain.account.user.entity;

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
public class UserFollow extends BaseEntity {
    // 팔로우 하는 유저
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

    // 팔로우 당하는 유저
    @ManyToOne
    @JoinColumn(name = "followee_id")
    private User followee;
}
