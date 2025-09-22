package com.letzgo.LetzgoBe.domain.map.dto.req;

import com.letzgo.LetzgoBe.domain.map.entity.Review;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private String title;
    private int rating;
    private String content;

    public static ReviewRequest entitytoDto(Review review) {
        return ReviewRequest.builder()
                .title(review.getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .build();
    }
}
