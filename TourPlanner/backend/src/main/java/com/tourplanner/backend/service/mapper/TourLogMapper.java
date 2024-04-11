package com.tourplanner.backend.service.mapper;

import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.service.dto.TourLogDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TourLogMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Convert from TourLogDto to TourLogEntity
    public TourLogEntity toEntity(TourLogDto dto) {
        if (dto == null) {
            return null;
        }

        TourLogEntity entity = new TourLogEntity();
        entity.setId(dto.getId());
      //  entity.setTourId(dto.getTourId());
        entity.setDateTime(LocalDateTime.parse(dto.getDateTime(), formatter));
        entity.setComment(dto.getComment());
        entity.setDifficulty(dto.getDifficulty());
        entity.setTotalDistance(dto.getTotalDistance());
        entity.setTotalTime(dto.getTotalTime());
        entity.setRating(dto.getRating());

        return entity;
    }

    // Convert from TourLogEntity to TourLogDto
    public TourLogDto toDto(TourLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return TourLogDto.builder()
                .id(entity.getId())
          //      .tourId(entity.getTourId())
                .dateTime(entity.getDateTime().format(formatter))
                .comment(entity.getComment())
                .difficulty(entity.getDifficulty())
                .totalDistance(entity.getTotalDistance())
                .totalTime(entity.getTotalTime())
                .rating(entity.getRating())
                .build();
    }
}

//The `TourLogMapper` class in the `com.tourplanner.backend.service.mapper` package provides bidirectional mapping between `TourLogEntity` and `TourLogDto`.
// It facilitates the conversion of data transfer objects (DTOs) to entities for persistence in the database and vice versa,
// ensuring separation of concerns between the application's persistence model and its representation in the API.
// This mapper utilizes a `DateTimeFormatter` to handle the conversion of date and time fields between string representation in DTOs and `LocalDateTime` in entities.