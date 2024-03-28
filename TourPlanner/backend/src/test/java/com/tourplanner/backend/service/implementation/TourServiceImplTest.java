package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourMapper tourMapper;

    @InjectMocks
    private TourServiceImpl tourService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenGetAllToursCalled_thenReturnDtoList() {
        // Arrange
        List<TourEntity> tourEntities = Arrays.asList(new TourEntity());
        List<TourDto> tourDtos = Arrays.asList(new TourDto());

        when(tourRepository.findAll()).thenReturn(tourEntities);
        when(tourMapper.mapToDto(tourEntities.get(0))).thenReturn(tourDtos.get(0));

        // Act
        List<TourDto> result = tourService.getAllTours();

        // Assert
        assertEquals(tourDtos, result);
        verify(tourRepository).findAll();
        verify(tourMapper).mapToDto(tourEntities.get(0));
    }

    @Test
    void whenSaveNewTourCalled_thenRepositorySaveCalled() {
        // Arrange
        TourDto tourDto = new TourDto();
        TourEntity tourEntity = new TourEntity();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        // Act
        tourService.saveNewTour(tourDto);

        // Assert
        verify(tourRepository).save(any(TourEntity.class));
    }

    @Test
    void whenSaveNewTourCalled_withNullDto_thenThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tourService.saveNewTour(null));
    }

    @Test
    void whenGetAllToursCalled_andNoToursExist_thenReturnEmptyList() {
        // Arrange
        when(tourRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<TourDto> result = tourService.getAllTours();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetAllToursCalled_thenReturnCorrectDtoProperties() {
        // Arrange
        TourEntity tourEntity = new TourEntity(1L, "Sample Tour", 100L);
        TourDto tourDto = new TourDto(1L, "Sample Tour", 100L);

        when(tourRepository.findAll()).thenReturn(Arrays.asList(tourEntity));
        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);

        // Act
        List<TourDto> result = tourService.getAllTours();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(tourEntity.getId(), result.get(0).getId());
        assertEquals(tourEntity.getName(), result.get(0).getName());
        assertEquals(tourEntity.getDistance(), result.get(0).getDistance());
    }
}
