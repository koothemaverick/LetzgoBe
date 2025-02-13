package com.letzgo.LetzgoBe.domain.map.map.entity;

import com.letzgo.LetzgoBe.common.entity.Account;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int review_id;

    @OneToOne
    @JoinColumn(name = "account_pk")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "place_pk")
    private Place place;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private LocalTime created_at;
}
