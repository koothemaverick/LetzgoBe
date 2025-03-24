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

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_pk")
    private Place place;

    @Column(name = "photo_dir", nullable = false)
    private String photoDir; //없을경우 "null"

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    public void update(String title, String photoDir, String content, int rating) {
        this.title = title;
        this.photoDir = photoDir;
        this.content = photoDir;
        this.rating = rating;
    }
}
