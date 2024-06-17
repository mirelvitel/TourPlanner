package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.persistence.repository.TourLogRepository;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourLogMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class TourLogServiceImplTest {

    @Mock
    private TourLogRepository tourLogRepository;

    @Mock
    private TourLogMapper tourLogMapper;

    @InjectMocks
    private TourLogServiceImpl tourLogService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddTourLog() {
        TourLogDto tourLogDto = new TourLogDto();
        tourLogDto.setComment("Test Log");
        TourLogEntity tourLogEntity = new TourLogEntity();
        when(tourLogMapper.toEntity(tourLogDto)).thenReturn(tourLogEntity);
        when(tourLogRepository.save(tourLogEntity)).thenReturn(tourLogEntity);

        tourLogService.addTourLog(tourLogDto);

        verify(tourLogRepository, times(1)).save(tourLogEntity);
    }

    @Test
    public void testGetTourLogsByTourId() {
        Long tourId = 1L;
        TourLogEntity tourLogEntity = new TourLogEntity();
        tourLogEntity.setTour(new TourEntity());
        tourLogEntity.getTour().setId(tourId);
        when(tourLogRepository.findAll()).thenReturn(Arrays.asList(tourLogEntity));
        TourLogDto tourLogDto = new TourLogDto();
        when(tourLogMapper.toDto(tourLogEntity)).thenReturn(tourLogDto);

        List<TourLogDto> result = tourLogService.getTourLogsByTourId(tourId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateTourLog() {
        TourLogDto tourLogDto = new TourLogDto();
        tourLogDto.setId(1L);
        TourLogEntity tourLogEntity = new TourLogEntity();
        when(tourLogRepository.existsById(tourLogDto.getId())).thenReturn(true);
        when(tourLogMapper.toEntity(tourLogDto)).thenReturn(tourLogEntity);
        when(tourLogRepository.save(tourLogEntity)).thenReturn(tourLogEntity);

        tourLogService.updateTourLog(tourLogDto);

        verify(tourLogRepository, times(1)).save(tourLogEntity);
    }

    @Test
    public void testDeleteTourLog() {
        Long logId = 1L;
        when(tourLogRepository.existsById(logId)).thenReturn(true);
        doNothing().when(tourLogRepository).deleteById(logId);

        tourLogService.deleteTourLog(logId);

        verify(tourLogRepository, times(1)).deleteById(logId);
    }

    @Test
    public void testValidateTourLogExists() {
        Long logId = 1L;
        when(tourLogRepository.existsById(logId)).thenReturn(true);

        tourLogService.validateTourLogExists(logId);

        verify(tourLogRepository, times(1)).existsById(logId);
    }

    @Test
    public void testValidateTourLogNotFound() {
        Long logId = 1L;
        when(tourLogRepository.existsById(logId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourLogService.validateTourLogExists(logId);
        });

        assertEquals("Tour log not found with id " + logId, exception.getMessage());
    }
    @Test
    public void testUpdateTourLogWithValidData() {
        TourLogDto tourLogDto = new TourLogDto();
        tourLogDto.setId(1L);
        tourLogDto.setComment("Updated Log");
        TourLogEntity tourLogEntity = new TourLogEntity();
        when(tourLogRepository.existsById(tourLogDto.getId())).thenReturn(true);
        when(tourLogMapper.toEntity(tourLogDto)).thenReturn(tourLogEntity);
        when(tourLogRepository.save(tourLogEntity)).thenReturn(tourLogEntity);

        tourLogService.updateTourLog(tourLogDto);

        verify(tourLogRepository, times(1)).save(tourLogEntity);
    }
    @Test
    public void testGetTourLogsWhenNoLogsExistForTour() {
        Long tourId = 1L;
        when(tourLogRepository.findAll()).thenReturn(Collections.emptyList());

        List<TourLogDto> result = tourLogService.getTourLogsByTourId(tourId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAddTourLogWithNullDto() {
        TourLogDto tourLogDto = null;

        assertThrows(NullPointerException.class, () -> {
            tourLogService.addTourLog(tourLogDto);
        });
    }


    @Test
    public void testGetTourLogsByTourIdWithNoMatchingTourId() {
        Long tourId = 1L;
        TourLogEntity tourLogEntity = new TourLogEntity();
        tourLogEntity.setTour(new TourEntity());
        tourLogEntity.getTour().setId(2L); // Different ID
        when(tourLogRepository.findAll()).thenReturn(Arrays.asList(tourLogEntity));

        List<TourLogDto> result = tourLogService.getTourLogsByTourId(tourId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteTourLogWithNonExistentLogId() {
        Long logId = 1L;
        when(tourLogRepository.existsById(logId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourLogService.deleteTourLog(logId);
        });

        assertEquals("Tour log not found with id " + logId, exception.getMessage());
    }

}
