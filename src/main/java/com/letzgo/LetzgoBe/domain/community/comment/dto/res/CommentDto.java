package com.letzgo.LetzgoBe.domain.community.comment.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Long likeCount;
    private String content;
    private Long superCommentId;
    private Boolean liked;
    private LocalDateTime createdAt;
}
