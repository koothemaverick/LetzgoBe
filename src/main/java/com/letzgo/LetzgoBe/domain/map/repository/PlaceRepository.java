package com.letzgo.LetzgoBe.domain.map.repository;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    public Place findByPlaceId(String placeId);
    public Optional<Place> findById(Long id);
    public Place save(Place place);
}
