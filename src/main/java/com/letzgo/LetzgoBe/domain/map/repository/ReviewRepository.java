package com.letzgo.LetzgoBe.domain.map.repository;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import com.letzgo.LetzgoBe.domain.map.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    public List<Review> findByPlace(Place place);
    public Review save(Review review);
}
