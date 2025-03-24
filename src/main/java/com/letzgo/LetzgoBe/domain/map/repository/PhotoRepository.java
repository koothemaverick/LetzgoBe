package com.letzgo.LetzgoBe.domain.map.repository;

import com.letzgo.LetzgoBe.domain.map.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
