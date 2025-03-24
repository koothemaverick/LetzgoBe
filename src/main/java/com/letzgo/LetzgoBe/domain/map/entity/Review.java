package com.letzgo.LetzgoBe.domain.map.entity;

import com.letzgo.LetzgoBe.domain.account.member.entity.Member;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_pk")
    private Member member;

    @OneToOne
    @JoinColumn(name = "photo_pk")
    private Photo photo; //없을경우 null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk")
    private Place place;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    public void update(Photo photo,String title, String content, int rating) {
        this.photo = photo;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public void update(String title, String content, int rating) {
        this.title = title;
        this.content = content;
        this.rating = rating;
    }
}
