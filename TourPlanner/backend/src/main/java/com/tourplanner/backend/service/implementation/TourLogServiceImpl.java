package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.persistence.repository.TourLogRepository;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourLogMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourLogMapper tourLogMapper;

    @Autowired
    public TourLogServiceImpl(TourLogRepository tourLogRepository, TourLogMapper tourLogMapper) {
        this.tourLogRepository = tourLogRepository;
        this.tourLogMapper = tourLogMapper;
    }

    @Override
    public void addTourLog(TourLogDto tourLogDto) {
        TourLogEntity tourLogEntity = tourLogMapper.toEntity(tourLogDto);
        tourLogRepository.save(tourLogEntity);
    }

    @Override
    public List<TourLogDto> getTourLogsByTourId(Long tourId) {
        List<TourLogEntity> tourLogEntities = tourLogRepository.findAll().stream()
                .filter(tourLog -> tourLog.getTourId().equals(tourId))
                .collect(Collectors.toList());
        return tourLogEntities.stream().map(tourLogMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateTourLog(TourLogDto tourLogDto) {
        validateTourLogExists(tourLogDto.getId());
        TourLogEntity tourLogEntity = tourLogMapper.toEntity(tourLogDto);
        tourLogRepository.save(tourLogEntity);
    }

    protected void validateTourLogExists(Long id) {
        if (!tourLogRepository.existsById(id)) {
            throw new EntityNotFoundException("Tour log not found with id " + id);
        }
    }


    @Override
    public void deleteTourLog(Long logId) {
        if (!tourLogRepository.existsById(logId)) {
            throw new EntityNotFoundException("Tour log not found with id " + logId);
        }
        tourLogRepository.deleteById(logId);
    }
}


//This file defines the `TourLogServiceImpl` class, a service layer implementation that manages tour log data for the Tour Planner application.
// It provides functionalities to add, retrieve, update, and delete tour logs,
// leveraging a `TourLogRepository` for database interactions and a `TourLogMapper` for converting between entity and DTO forms.
// Additionally, it includes validation to ensure tour logs exist before performing updates or deletions,
// throwing an `EntityNotFoundException` if a log cannot be found.