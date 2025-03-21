package com.letzgo.LetzgoBe.domain.map.dto;

import com.letzgo.LetzgoBe.domain.map.entity.Review;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private String account;
    private String title;
    private int rating;
    private String content;
    private String photo_dir; //없을경우 "null"

    public static ReviewDto entitytoDto(Review review) {
        return ReviewDto.builder()
                .account(review.getUser().getName())
                .title(review.getContent())
                .rating(review.getRating())
                .content(review.getContent())
                .photo_dir(review.getPhoto_dir())
                .build();
    }
}
