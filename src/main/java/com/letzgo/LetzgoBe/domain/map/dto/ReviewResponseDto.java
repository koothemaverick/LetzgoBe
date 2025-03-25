package com.letzgo.LetzgoBe.domain.map.dto;

import com.letzgo.LetzgoBe.domain.map.entity.Review;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String account; //계정명
    private String title;
    private int rating;
    private String content;
    private String photoDir; //없을경우 "null"

    public static ReviewResponseDto entitytoDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .account(review.getMember().getName())
                .title(review.getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .photoDir(review.getPhoto() != null ? review.getPhoto().getStore_dir() : null)
                .build();
    }
}
