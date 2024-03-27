package com.tourplanner.backend.service;

import com.tourplanner.backend.service.dto.TourDto;

import java.util.List;

public interface TourService {

    void saveNewTour(TourDto tourDto);
    List<TourDto> getAllTours();
}
