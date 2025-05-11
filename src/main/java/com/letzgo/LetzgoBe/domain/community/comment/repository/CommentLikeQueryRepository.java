package com.letzgo.LetzgoBe.domain.community.comment.repository;

public interface CommentLikeQueryRepository {
    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);
    long countByCommentId(Long commentId);
}
