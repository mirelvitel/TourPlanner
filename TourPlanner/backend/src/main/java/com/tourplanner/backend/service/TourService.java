package com.tourplanner.backend.service;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.service.dto.TourDto;

import java.util.List;
import java.util.Optional;

public interface TourService {

    void addNewTour(TourDto tourDto);

    void deleteTourById(Long id);

    void updateTour(TourDto tourDto);

    TourDto getTourById(Long id);

    List<TourDto> getAllTours();
}
