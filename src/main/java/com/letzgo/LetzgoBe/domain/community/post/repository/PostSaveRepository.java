package com.letzgo.LetzgoBe.domain.community.post.repository;

import com.letzgo.LetzgoBe.domain.community.post.entity.PostSave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostSaveRepository extends JpaRepository<PostSave, Long> {
}
