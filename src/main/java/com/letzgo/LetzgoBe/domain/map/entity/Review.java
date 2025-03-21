package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_pk")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk")
    private Place place;

    @Column(nullable = false)
    private String photo_dir; //없을경우 "null"

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

}
