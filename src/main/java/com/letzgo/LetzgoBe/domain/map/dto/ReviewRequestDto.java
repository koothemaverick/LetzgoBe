package com.letzgo.LetzgoBe.domain.map.dto;

import com.letzgo.LetzgoBe.domain.map.entity.Review;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {
    private String title;
    private int rating;
    private String content;

    public static ReviewRequestDto entitytoDto(Review review) {
        return ReviewRequestDto.builder()
                .title(review.getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .build();
    }
}
