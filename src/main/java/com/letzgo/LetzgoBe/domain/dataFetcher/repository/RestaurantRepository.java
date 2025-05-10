package com.letzgo.LetzgoBe.domain.dataFetcher.repository;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByRegion(String Region);
}
