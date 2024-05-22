package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
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
        return tourMapper.mapToDto(
                tourRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tour not found with id " + id))
        );
    }

    @Override
    public List<TourDto> getAllTours() {
        return tourRepository.findAll().stream()
                .map(tourMapper::mapToDto)
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
}
