package com.letzgo.LetzgoBe.domain.community.comment.repository;

import com.letzgo.LetzgoBe.domain.community.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}
