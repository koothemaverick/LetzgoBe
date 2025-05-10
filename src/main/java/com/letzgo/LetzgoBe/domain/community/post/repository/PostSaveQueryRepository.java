package com.letzgo.LetzgoBe.domain.community.post.repository;

public interface PostSaveQueryRepository {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
