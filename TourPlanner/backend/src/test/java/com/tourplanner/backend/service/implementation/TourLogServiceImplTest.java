package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourLogEntity;
import com.tourplanner.backend.persistence.repository.TourLogRepository;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourLogServiceImplTest {

    @Mock
    private TourLogRepository tourLogRepository;

    @Mock
    private TourLogMapper tourLogMapper;

    @InjectMocks
    private TourLogServiceImpl tourLogService;

    @Test
    void whenAddTourLogCalled_thenRepositorySaveCalled() {
        TourLogDto tourLogDto = new TourLogDto();
        TourLogEntity tourLogEntity = new TourLogEntity();

        when(tourLogMapper.toEntity(tourLogDto)).thenReturn(tourLogEntity);
        when(tourLogRepository.save(any(TourLogEntity.class))).thenReturn(tourLogEntity);

        tourLogService.addTourLog(tourLogDto);

        verify(tourLogRepository).save(any(TourLogEntity.class));
    }

    @Test
    void whenUpdateTourLogCalled_thenRepositorySaveCalledWithCorrectTourLogEntity() {
        // Arrange
        final Long tourLogId = 1L;
        TourLogDto tourLogDto = new TourLogDto();
        tourLogDto.setId(tourLogId);
        tourLogDto.setComment("Nice tour");

        TourLogEntity mockEntity = new TourLogEntity();
        mockEntity.setId(tourLogDto.getId());
        mockEntity.setComment(tourLogDto.getComment());

        when(tourLogRepository.existsById(tourLogId)).thenReturn(true); // Mocking to return true to pass the validation
        when(tourLogMapper.toEntity(any(TourLogDto.class))).thenReturn(mockEntity);
        when(tourLogRepository.save(any(TourLogEntity.class))).thenReturn(mockEntity);

        // Act
        tourLogService.updateTourLog(tourLogDto);

        // Assert
        ArgumentCaptor<TourLogEntity> captor = ArgumentCaptor.forClass(TourLogEntity.class);
        verify(tourLogRepository).save(captor.capture());
        TourLogEntity capturedEntity = captor.getValue();

        assertEquals(tourLogDto.getId(), capturedEntity.getId());
        assertEquals(tourLogDto.getComment(), capturedEntity.getComment());
    }

    @Test
    void whenDeleteTourLogCalled_thenRepositoryDeleteByIdCalled() {
        Long logId = 1L;

        when(tourLogRepository.existsById(logId)).thenReturn(true);

        tourLogService.deleteTourLog(logId);

        verify(tourLogRepository).deleteById(logId);
    }
}
