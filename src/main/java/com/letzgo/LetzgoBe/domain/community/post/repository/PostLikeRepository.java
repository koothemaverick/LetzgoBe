package com.letzgo.LetzgoBe.domain.community.post.repository;

import com.letzgo.LetzgoBe.domain.community.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
