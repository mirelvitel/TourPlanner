package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    void whenSaveNewTourCalled_thenRepositorySaveCalled() {
        // Arrange
        TourDto tourDto = new TourDto();
        TourEntity tourEntity = new TourEntity();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        // Act
        tourService.addNewTour(tourDto);

        // Assert
        verify(tourRepository).save(any(TourEntity.class));
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
    void whenDeleteTourCalled_thenRepositoryDeleteByIdCalled() {
        // Arrange
        Long tourId = 1L;

        // Act
        tourService.deleteTourById(tourId);

        // Assert
        verify(tourRepository).deleteById(tourId);
    }

    @Test
    void whenUpdateTourCalled_thenRepositorySaveCalledWithCorrectTourEntity() {
        // Arrange
        TourDto tourDto = new TourDto();
        tourDto.setId(1L);
        tourDto.setName("Name");
        tourDto.setDistance(100L);

        TourEntity expectedTourEntity = TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .distance(tourDto.getDistance())
                .build();

        when(tourRepository.save(any(TourEntity.class))).thenReturn(expectedTourEntity);

        // Act
        tourService.updateTour(tourDto);

        // Assert
        ArgumentCaptor<TourEntity> tourEntityArgumentCaptor = ArgumentCaptor.forClass(TourEntity.class);
        verify(tourRepository).save(tourEntityArgumentCaptor.capture());
        TourEntity capturedEntity = tourEntityArgumentCaptor.getValue();

        assertEquals(tourDto.getId(), capturedEntity.getId());
        assertEquals(tourDto.getName(), capturedEntity.getName());
        assertEquals(tourDto.getDistance(), capturedEntity.getDistance());
    }

    @Test
    void whenGetTourByIdCalled_andTourExists_thenReturnCorrectTourDto() {
        // Arrange
        Long tourId = 1L;
        TourEntity expectedTourEntity = new TourEntity();
        expectedTourEntity.setId(tourId);
        // Assume expectedTourEntity has more fields that you'd set here for a real test

        TourDto expectedTourDto = new TourDto();
        expectedTourDto.setId(tourId);
        // Similarly, set other fields on expectedTourDto as needed

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(expectedTourEntity));
        when(tourMapper.mapToDto(expectedTourEntity)).thenReturn(expectedTourDto);

        // Act
        TourDto result = tourService.getTourById(tourId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedTourDto.getId(), result.getId());
        // Optionally, assert other fields are equal as needed
    }

    @Test
    void whenGetTourByIdCalled_andTourDoesNotExist_thenThrowEntityNotFoundException() {
        // Arrange
        Long tourId = 1L;
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            tourService.getTourById(tourId);
        });
    }

}
