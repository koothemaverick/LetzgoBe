package com.letzgo.LetzgoBe.domain.community.post.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DetailPostDto {
    private Long memberId;
    private Long id;
    private String nickname;
    private String profileImageUrl;
    private Long likeCount;
    private Long commentCount;
    private Double mapX;
    private Double mapY;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
}
