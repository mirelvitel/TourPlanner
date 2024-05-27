package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.persistence.repository.TourLogRepository;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourLogMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourLogServiceImpl implements TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourLogMapper tourLogMapper;

    private static final Logger logger = Logger.getLogger(TourLogServiceImpl.class);

    @Autowired
    public TourLogServiceImpl(TourLogRepository tourLogRepository, TourLogMapper tourLogMapper) {
        this.tourLogRepository = tourLogRepository;
        this.tourLogMapper = tourLogMapper;
    }

    @Override
    public void addTourLog(TourLogDto tourLogDto) {
        TourLogEntity tourLogEntity = tourLogMapper.toEntity(tourLogDto);
        tourLogRepository.save(tourLogEntity);
        logger.info("Tour log added: " + tourLogDto.getComment());
    }

    @Override
    public List<TourLogDto> getTourLogsByTourId(Long tourId) {
        logger.info("Fetching tour logs for tour id: " + tourId);
        List<TourLogEntity> tourLogEntities = tourLogRepository.findAll().stream()
                .filter(tourLog -> tourLog.getTour().getId().equals(tourId))
                .collect(Collectors.toList());
        List<TourLogDto> tourLogDtos = tourLogEntities.stream().map(tourLogMapper::toDto).collect(Collectors.toList());
        logger.info("Total tour logs found for tour id " + tourId + ": " + tourLogDtos.size());
        return tourLogDtos;
    }

    @Override
    public void updateTourLog(TourLogDto tourLogDto) {
        validateTourLogExists(tourLogDto.getId());
        TourLogEntity tourLogEntity = tourLogMapper.toEntity(tourLogDto);
        tourLogRepository.save(tourLogEntity);
        logger.info("Tour log updated with id: " + tourLogDto.getId());
    }

    protected void validateTourLogExists(Long id) {
        if (!tourLogRepository.existsById(id)) {
            logger.error("Tour log not found with id: " + id);
            throw new EntityNotFoundException("Tour log not found with id " + id);
        }
    }

    @Override
    public void deleteTourLog(Long logId) {
        if (!tourLogRepository.existsById(logId)) {
            logger.error("Tour log not found with id: " + logId);
            throw new EntityNotFoundException("Tour log not found with id " + logId);
        }
        tourLogRepository.deleteById(logId);
        logger.info("Tour log deleted with id: " + logId);
    }
}
