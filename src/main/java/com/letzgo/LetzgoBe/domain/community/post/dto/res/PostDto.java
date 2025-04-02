package com.letzgo.LetzgoBe.domain.community.post.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private Double mapX;
    private Double mapY;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
