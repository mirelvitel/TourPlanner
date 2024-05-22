package com.tourplanner.backend.service.mapper;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.dto.TourLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TourLogMapper {

    @Autowired
    private TourRepository tourRepository;
    public TourLogEntity toEntity(TourLogDto dto) {
        if (dto == null) {
            return null;
        }

        TourLogEntity entity = new TourLogEntity();
        entity.setId(dto.getId());
        TourEntity tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        entity.setTour(tour);
        entity.setDateTime(dto.getDateTime());
        entity.setComment(dto.getComment());
        entity.setDifficulty(dto.getDifficulty());
        entity.setTotalDistance(dto.getTotalDistance());
        entity.setTotalTime(dto.getTotalTime());
        entity.setRating(dto.getRating());

        return entity;
    }

    public TourLogDto toDto(TourLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return TourLogDto.builder()
                .id(entity.getId())
                .tourId(entity.getTour().getId())
                .dateTime(entity.getDateTime())
                .comment(entity.getComment())
                .difficulty(entity.getDifficulty())
                .totalDistance(entity.getTotalDistance())
                .totalTime(entity.getTotalTime())
                .rating(entity.getRating())
                .build();
    }
}