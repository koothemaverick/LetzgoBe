package com.letzgo.LetzgoBe.domain.dataFetcher.repository;
import com.letzgo.LetzgoBe.domain.dataFetcher.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByRegion(String region);
}
