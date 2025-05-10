package com.letzgo.LetzgoBe.domain.community.post.repository;

import com.letzgo.LetzgoBe.domain.community.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
}
