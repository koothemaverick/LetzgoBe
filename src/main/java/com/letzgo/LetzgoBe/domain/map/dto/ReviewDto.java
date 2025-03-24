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
    private Long id;
    private String account; //계정명
    private String title;
    private int rating;
    private String content;
    private String photoDir; //없을경우 "null"

    public static ReviewDto entitytoDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .account(review.getUser().getName())
                .title(review.getContent())
                .rating(review.getRating())
                .content(review.getContent())
                .photoDir(review.getPhotoDir())
                .build();
    }
}
