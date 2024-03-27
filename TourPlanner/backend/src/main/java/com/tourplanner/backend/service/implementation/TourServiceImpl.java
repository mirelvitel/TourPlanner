package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TourServiceImpl implements TourService {
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourMapper tourMapper;

    @Override
    public void saveNewTour(TourDto tourDto) {
        TourEntity entity = TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .distance(tourDto.getDistance())
                .build();
        tourRepository.save(entity);
    }

    @Override
    public List<TourDto> getAllTours() {
        return tourMapper.mapToDto(tourRepository.findAll());
    }


}
