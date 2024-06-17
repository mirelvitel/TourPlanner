package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TourServiceImpl implements TourService {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourMapper tourMapper;

    @Autowired
    private TourLogService tourLogService;

    private static final Logger logger = Logger.getLogger(TourServiceImpl.class);

    @Override
    public void addNewTour(TourDto tourDto) {
        if (tourDto.getName() == null || tourDto.getName().isEmpty()) {
            throw new RuntimeException("Tour name cannot be null or empty");
        }
        TourEntity entity = mapToEntity(tourDto);
        tourRepository.save(entity);
        logger.info("New tour added: " + tourDto.getName());
    }

    @Override
    public void deleteTourById(Long id) {
        if (!tourRepository.existsById(id)) {
            throw new RuntimeException("Tour not found");
        }
        try {
            tourRepository.deleteById(id);
            logger.info("Tour deleted with id: " + id);
        } catch (Exception e) {
            logger.error("Error deleting tour with id: " + id, e);
            throw new RuntimeException("Error deleting tour with id: " + id, e);
        }
    }

    @Override
    public void updateTour(TourDto tourDto) {
        if (tourDto.getId() == null) {
            throw new RuntimeException("Tour ID cannot be null");
        }
        TourEntity entity = mapToEntity(tourDto);
        tourRepository.save(entity);
        logger.info("Tour updated: " + tourDto.getName());
    }

    @Override
    public TourDto getTourById(Long id) {
        logger.info("Fetching tour with id: " + id);
        TourEntity tourEntity = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id " + id));
        logger.info("Tour found: " + tourEntity.getName());
        return computeAttributes(tourMapper.mapToDto(tourEntity));
    }

    @Override
    public List<TourDto> getAllTours() {
        logger.info("Fetching all tours");
        List<TourDto> tours = tourRepository.findAll().stream()
                .map(tourMapper::mapToDto)
                .map(this::computeAttributes)
                .collect(Collectors.toList());
        logger.info("Total tours found: " + tours.size());
        return tours;
    }

    private TourEntity mapToEntity(TourDto tourDto) {
        return TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .tourDistance(tourDto.getTourDistance())
                .tourDescription(tourDto.getTourDescription())
                .transportType(tourDto.getTransportType())
                .endLocation(tourDto.getEndLocation())
                .startLocation(tourDto.getStartLocation())
                .build();
    }

    public TourDto computeAttributes(TourDto tourDto) {
        List<TourLogDto> tourLogs = tourLogService.getTourLogsByTourId(tourDto.getId());
        tourDto.setTourLogs(tourLogs);
        tourDto.setPopularity(computePopularity(tourLogs));
        tourDto.setChildFriendliness(computeChildFriendliness(tourLogs));
        return tourDto;
    }

    public int computePopularity(List<TourLogDto> logs) {
        return logs.size();
    }

    public double computeChildFriendliness(List<TourLogDto> logs) {
        if (logs.isEmpty()) {
            return 0;
        }
        double totalDifficulty = logs.stream().mapToDouble(TourLogDto::getDifficulty).sum();
        double totalDistance = logs.stream().mapToDouble(TourLogDto::getTotalDistance).sum();
        double totalTime = logs.stream().mapToDouble(TourLogDto::getTotalTime).sum();


        double normalizedDifficulty = 1 - (totalDifficulty / (logs.size() * 5)); // Max difficulty is 5
        double normalizedDistance = 1 - (totalDistance / (logs.size() * 100)); // Max distance suitable for children is 100 km
        double normalizedTime = 1 - (totalTime / (logs.size() * 10)); // Max time suitable for children is 10 hours


        return (normalizedDifficulty * 0.5) + (normalizedDistance * 0.3) + (normalizedTime * 0.2);
    }
}
