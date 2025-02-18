package com.letzgo.LetzgoBe.domain.map.dto;

import lombok.*;

@ToString
@Builder
public class ReviewDto {
    private int review_id;
    private String account;
    private String title;
    private int rating;
    private String content;
}
