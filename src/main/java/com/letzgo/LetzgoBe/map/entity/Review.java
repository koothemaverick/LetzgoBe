package com.letzgo.LetzgoBe.map.entity;

import com.letzgo.LetzgoBe.common.entity.Account;
import jakarta.persistence.*;

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
    private int created_at;
}
