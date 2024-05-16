package com.tourplanner.backend.service;

import com.tourplanner.backend.service.dto.TourLogDto;

import java.util.List;

public interface TourLogService {
    void addTourLog(TourLogDto tourLogDto);

    List<TourLogDto> getTourLogsByTourId(Long tourId);

    void updateTourLog(TourLogDto tourLogDto);

    void deleteTourLog(Long logId);
}

//The `TourLogService` interface defines the contract for managing tour logs within the application.
// It outlines methods for adding, retrieving, updating, and deleting tour logs,
// ensuring these operations are abstracted from the underlying data storage mechanism.
// This interface facilitates interaction with tour log data,
// enabling the application to maintain a clear separation between the service layer and other components.