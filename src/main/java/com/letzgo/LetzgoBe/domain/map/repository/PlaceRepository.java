package com.letzgo.LetzgoBe.domain.map.repository;

import com.letzgo.LetzgoBe.domain.map.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    public Place findByPlaceId(String placeId);
    public Place save(Place place);
}
