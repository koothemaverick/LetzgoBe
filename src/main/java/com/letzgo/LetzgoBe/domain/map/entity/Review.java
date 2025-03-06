package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.domain.account.user.entity.User;
import com.letzgo.LetzgoBe.global.jpa.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Review extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "account_pk")
    private User user;

    @ManyToOne
    @JoinColumn(name = "place_pk")
    private Place place;

    @Column(nullable = true)
    private int photo; //리뷰 사진 존재시 1, 없을시 0

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;
}
