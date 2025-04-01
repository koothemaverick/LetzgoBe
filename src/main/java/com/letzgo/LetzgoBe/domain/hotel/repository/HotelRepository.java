package com.letzgo.LetzgoBe.domain.hotel.repository;
import com.letzgo.LetzgoBe.domain.hotel.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    public Hotel save();
}
