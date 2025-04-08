package com.letzgo.LetzgoBe.domain.schedule.repository;

import com.letzgo.LetzgoBe.domain.schedule.entity.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemTypeRepository extends JpaRepository<ItemType, Long> {
    Optional<ItemType> findByTypeName(String typeName);
}