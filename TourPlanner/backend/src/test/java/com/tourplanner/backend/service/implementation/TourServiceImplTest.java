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

    // Mocking dependencies
    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourMapper tourMapper;

    // Inject mocks into the tested service implementation
    @InjectMocks
    private TourServiceImpl tourService;

    // Initializes mocks before each test case
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests that the repository's save method is called when a new tour is added
    @Test
    void whenSaveNewTourCalled_thenRepositorySaveCalled() {
        //Setting up DTO and entity and mocking behavior
        TourDto tourDto = new TourDto();
        TourEntity tourEntity = new TourEntity();

        // Mock mapper and repository behavior
        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        //Attempt to add a new tour
        tourService.addNewTour(tourDto);

        //Verify that save was indeed called on the repository
        verify(tourRepository).save(any(TourEntity.class));
    }

    // Tests that an empty list is returned when there are no tours to retrieve
    @Test
    void whenGetAllToursCalled_andNoToursExist_thenReturnEmptyList() {
        // Mock repository to return an empty list
        when(tourRepository.findAll()).thenReturn(Arrays.asList());

        // Retrieve all tours
        List<TourDto> result = tourService.getAllTours();

        // Verify the result is an empty list
        assertTrue(result.isEmpty());
    }

    // Tests that the repository's deleteById method is called with the correct ID
    @Test
    void whenDeleteTourCalled_thenRepositoryDeleteByIdCalled() {
        // Provide a sample tour ID
        Long tourId = 1L;

        // Attempt to delete a tour by ID
        tourService.deleteTourById(tourId);

        // Verify that deleteById was called on the repository with the correct ID
        verify(tourRepository).deleteById(tourId);
    }

    // Tests that updating a tour results in the correct entity being saved
    @Test
    void whenUpdateTourCalled_thenRepositorySaveCalledWithCorrectTourEntity() {
        // Setup DTO and expected entity
        TourDto tourDto = new TourDto();
        tourDto.setId(1L);
        tourDto.setName("Name");
        tourDto.setTourDistance(100L);

        TourEntity expectedTourEntity = TourEntity.builder()
                .id(tourDto.getId())
                .name(tourDto.getName())
                .tourDistance(tourDto.getTourDistance())
                .build();

        // Mock repository to return the expected entity
        when(tourRepository.save(any(TourEntity.class))).thenReturn(expectedTourEntity);

        // Attempt to update a tour
        tourService.updateTour(tourDto);

        // Capture and verify the entity passed to save
        ArgumentCaptor<TourEntity> tourEntityArgumentCaptor = ArgumentCaptor.forClass(TourEntity.class);
        verify(tourRepository).save(tourEntityArgumentCaptor.capture());
        TourEntity capturedEntity = tourEntityArgumentCaptor.getValue();

        // Assert fields are correctly set
        assertEquals(tourDto.getId(), capturedEntity.getId());
        assertEquals(tourDto.getName(), capturedEntity.getName());
        assertEquals(tourDto.getTourDistance(), capturedEntity.getTourDistance());
    }

    // Tests that a correct TourDto is returned for an existing tour ID
    @Test
    void whenGetTourByIdCalled_andTourExists_thenReturnCorrectTourDto() {
        // Setup entity, DTO, and mock repository and mapper behavior
        Long tourId = 1L;
        TourEntity expectedTourEntity = new TourEntity();
        expectedTourEntity.setId(tourId);

        TourDto expectedTourDto = new TourDto();
        expectedTourDto.setId(tourId);

        when(tourRepository.findById(tourId)).thenReturn(Optional.of(expectedTourEntity));
        when(tourMapper.mapToDto(expectedTourEntity)).thenReturn(expectedTourDto);

        // Retrieve tour by ID
        TourDto result = tourService.getTourById(tourId);

        // Verify the result matches the expected DTO
        assertNotNull(result);
        assertEquals(expectedTourDto.getId(), result.getId());
    }

    // Tests that an EntityNotFoundException is thrown when a non-existent tour ID is queried
    @Test
    void whenGetTourByIdCalled_andTourDoesNotExist_thenThrowEntityNotFoundException() {
        // Mock repository to return empty optional
        Long tourId = 1L;
        when(tourRepository.findById(tourId)).thenReturn(Optional.empty());

        // Verify that querying a non-existent ID throws the exception
        assertThrows(EntityNotFoundException.class, () -> {
            tourService.getTourById(tourId);
        });
    }
}
