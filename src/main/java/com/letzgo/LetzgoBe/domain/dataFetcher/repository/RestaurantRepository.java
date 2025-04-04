package com.letzgo.LetzgoBe.domain.dataFetcher.repository;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
