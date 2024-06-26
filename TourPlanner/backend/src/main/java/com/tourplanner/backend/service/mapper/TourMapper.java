package com.tourplanner.backend.service.mapper;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.service.dto.TourDto;
import org.springframework.stereotype.Component;

@Component
public class TourMapper extends AbstractMapper<TourEntity, TourDto> {
    @Override
    public TourDto mapToDto(TourEntity source) {
        return TourDto.builder()
                .id(source.getId())
                .name(source.getName())
                .tourDistance(source.getTourDistance())
                .tourDescription(source.getTourDescription())
                .transportType(source.getTransportType())
                .endLocation(source.getEndLocation())
                .startLocation(source.getStartLocation())
                .build();
    }
}
