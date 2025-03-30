package com.letzgo.LetzgoBe.domain.account.member.entity;

import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class MemberFollowReq extends BaseEntity {
    // 팔로우 요청한 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_req_id", nullable = false)
    private Member followReq;

    // 팔로우 요청 받은 멤버
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_rec_id", nullable = false)
    private Member followRec;
}
