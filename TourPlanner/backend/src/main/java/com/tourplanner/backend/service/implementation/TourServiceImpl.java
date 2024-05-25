package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    public void addNewTour(TourDto tourDto) {
        TourEntity entity = mapToEntity(tourDto);
        tourRepository.save(entity);
    }

    @Override
    public void deleteTourById(Long id) {
        tourRepository.deleteById(id);
    }

    @Override
    public void updateTour(TourDto tourDto) {
        TourEntity entity = mapToEntity(tourDto);
        tourRepository.save(entity);
    }

    @Override
    public TourDto getTourById(Long id) {
        System.out.println("ServiceImpl");
        TourEntity tourEntity = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found with id " + id));
        return computeAttributes(tourMapper.mapToDto(tourEntity));
    }

    @Override
    public List<TourDto> getAllTours() {
        return tourRepository.findAll().stream()
                .map(tourMapper::mapToDto)
                .map(this::computeAttributes)
                .collect(Collectors.toList());
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

    private TourDto computeAttributes(TourDto tourDto) {
        List<TourLogDto> tourLogs = tourLogService.getTourLogsByTourId(tourDto.getId());
        tourDto.setTourLogs(tourLogs);
        tourDto.setPopularity(computePopularity(tourLogs));
        tourDto.setChildFriendliness(computeChildFriendliness(tourLogs));
        return tourDto;
    }

    private int computePopularity(List<TourLogDto> logs) {
        return logs.size();
    }

    private double computeChildFriendliness(List<TourLogDto> logs) {
        if (logs.isEmpty()) {
            return 0;
        }
        double totalDifficulty = logs.stream().mapToDouble(TourLogDto::getDifficulty).sum();
        double totalDistance = logs.stream().mapToDouble(TourLogDto::getTotalDistance).sum();
        double totalTime = logs.stream().mapToDouble(TourLogDto::getTotalTime).sum();

        // Normalize values and compute child-friendliness
        double normalizedDifficulty = 1 - (totalDifficulty / (logs.size() * 5)); // Max difficulty is 5
        double normalizedDistance = 1 - (totalDistance / (logs.size() * 100)); // Max distance suitable for children is 100 km
        double normalizedTime = 1 - (totalTime / (logs.size() * 10)); // Max time suitable for children is 10 hours

        // Weighted average
        return (normalizedDifficulty * 0.5) + (normalizedDistance * 0.3) + (normalizedTime * 0.2);
    }
}
