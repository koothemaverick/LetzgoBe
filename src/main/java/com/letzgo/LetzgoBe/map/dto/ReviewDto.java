package com.letzgo.LetzgoBe.map.dto;

import lombok.Builder;

@Builder
public class ReviewDto {
    private int review_id;
    private String account;
    private String title;
    private int rating;
    private String content;
}
