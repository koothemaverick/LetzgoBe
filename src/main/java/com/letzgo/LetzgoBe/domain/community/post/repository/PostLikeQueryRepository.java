package com.letzgo.LetzgoBe.domain.community.post.repository;

public interface PostLikeQueryRepository {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
    long countByPostId(Long postId);
}
