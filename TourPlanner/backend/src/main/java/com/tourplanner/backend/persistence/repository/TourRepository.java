package com.tourplanner.backend.persistence.repository;

import com.tourplanner.backend.persistence.entity.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TourRepository extends JpaRepository<TourEntity, Long> {

}
