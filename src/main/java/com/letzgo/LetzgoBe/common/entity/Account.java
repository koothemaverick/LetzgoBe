package com.letzgo.LetzgoBe.common.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int account_pk;

    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private int gender;

    @Column(nullable = false)
    private int phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalTime birthdate;

    @Column(nullable = false)
    private LocalTime created_at;


}
