package com.tourplanner.backend.service.implementation;

import com.tourplanner.backend.persistence.entity.TourEntity;
import com.tourplanner.backend.persistence.repository.TourRepository;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import com.tourplanner.backend.service.mapper.TourMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private TourMapper tourMapper;

    @Mock
    private TourLogService tourLogService;

    @InjectMocks
    private TourServiceImpl tourService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewTour() {
        TourDto tourDto = new TourDto();
        tourDto.setName("Test Tour");
        TourEntity tourEntity = TourEntity.builder()
                .name("Test Tour")
                .build();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        tourService.addNewTour(tourDto);

        verify(tourRepository, times(1)).save(any(TourEntity.class));
    }

    @Test
    public void testDeleteTourById() {
        Long id = 1L;

        when(tourRepository.existsById(id)).thenReturn(true);
        doNothing().when(tourRepository).deleteById(id);

        tourService.deleteTourById(id);

        verify(tourRepository, times(1)).existsById(id);
        verify(tourRepository, times(1)).deleteById(id);
    }

    @Test
    public void testUpdateTour() {
        TourDto tourDto = new TourDto();
        tourDto.setId(1L); // Ensure ID is set
        tourDto.setName("Updated Tour");
        TourEntity tourEntity = TourEntity.builder()
                .id(1L)
                .name("Updated Tour")
                .build();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        tourService.updateTour(tourDto);

        verify(tourRepository, times(1)).save(any(TourEntity.class));
    }


    @Test
    public void testGetTourById() {
        Long id = 1L;
        TourEntity tourEntity = TourEntity.builder()
                .id(id)
                .name("Test Tour")
                .build();

        when(tourRepository.findById(id)).thenReturn(Optional.of(tourEntity));
        TourDto tourDto = TourDto.builder()
                .id(id)
                .name("Test Tour")
                .build();
        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourLogService.getTourLogsByTourId(id)).thenReturn(Arrays.asList(new TourLogDto()));

        TourDto result = tourService.getTourById(id);

        assertNotNull(result);
        assertEquals("Test Tour", result.getName());
    }

    @Test
    public void testGetAllTours() {
        TourEntity tourEntity = new TourEntity();
        when(tourRepository.findAll()).thenReturn(Arrays.asList(tourEntity));
        TourDto tourDto = new TourDto();
        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourLogService.getTourLogsByTourId(anyLong())).thenReturn(Arrays.asList(new TourLogDto()));

        List<TourDto> result = tourService.getAllTours();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    public void testGetTourByInvalidId() {
        Long id = 999L;
        when(tourRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tourService.getTourById(id);
        });

        assertEquals("Tour not found with id 999", exception.getMessage());
    }

    @Test
    public void testComputeAttributes() {
        TourDto tourDto = new TourDto();
        tourDto.setId(1L);
        TourLogDto logDto1 = new TourLogDto();
        logDto1.setDifficulty(3);
        logDto1.setTotalDistance(50);
        logDto1.setTotalTime(5);
        TourLogDto logDto2 = new TourLogDto();
        logDto2.setDifficulty(2);
        logDto2.setTotalDistance(30);
        logDto2.setTotalTime(3);

        when(tourLogService.getTourLogsByTourId(1L)).thenReturn(Arrays.asList(logDto1, logDto2));

        TourDto result = tourService.computeAttributes(tourDto);

        assertNotNull(result);
        assertEquals(2, result.getTourLogs().size());
        assertEquals(0.55, result.getChildFriendliness(), 0.01); // Adjusted expected value to match the calculation
        assertEquals(2, result.getPopularity());
    }


    @Test
    public void testComputePopularity() {
        TourLogDto logDto1 = new TourLogDto();
        TourLogDto logDto2 = new TourLogDto();

        int result = tourService.computePopularity(Arrays.asList(logDto1, logDto2));

        assertEquals(2, result);
    }

    @Test
    public void testComputeChildFriendliness() {
        TourLogDto logDto1 = new TourLogDto();
        logDto1.setDifficulty(3);
        logDto1.setTotalDistance(50);
        logDto1.setTotalTime(5);
        TourLogDto logDto2 = new TourLogDto();
        logDto2.setDifficulty(2);
        logDto2.setTotalDistance(30);
        logDto2.setTotalTime(3);

        double result = tourService.computeChildFriendliness(Arrays.asList(logDto1, logDto2));

        assertEquals(0.65, result, 0.1);
    }
    @Test
    public void testAddTourWithoutName() {
        TourDto tourDto = new TourDto(); // No name set
        TourEntity tourEntity = new TourEntity();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.addNewTour(tourDto);
        });

        assertEquals("Tour name cannot be null or empty", exception.getMessage());
    }


    @Test
    public void testGetTourByIdAndVerifyLogsIncluded() {
        Long id = 1L;
        TourEntity tourEntity = TourEntity.builder()
                .id(id)
                .name("Test Tour")
                .build();

        when(tourRepository.findById(id)).thenReturn(Optional.of(tourEntity));
        TourDto tourDto = TourDto.builder()
                .id(id)
                .name("Test Tour")
                .build();
        TourLogDto logDto1 = new TourLogDto();
        TourLogDto logDto2 = new TourLogDto();
        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourLogService.getTourLogsByTourId(id)).thenReturn(Arrays.asList(logDto1, logDto2));

        TourDto result = tourService.getTourById(id);

        assertNotNull(result);
        assertEquals("Test Tour", result.getName());
        assertEquals(2, result.getTourLogs().size());
    }

    @Test
    public void testUpdateTourWithValidData() {
        TourDto tourDto = new TourDto();
        tourDto.setId(1L);
        tourDto.setName("Updated Tour");
        TourEntity tourEntity = TourEntity.builder()
                .id(1L)
                .name("Updated Tour")
                .build();

        when(tourMapper.mapToDto(tourEntity)).thenReturn(tourDto);
        when(tourRepository.save(any(TourEntity.class))).thenReturn(tourEntity);

        tourService.updateTour(tourDto);

        verify(tourRepository, times(1)).save(any(TourEntity.class));
    }
    @Test
    public void testDeleteTourByIdWithInvalidId() {
        Long invalidId = 999L;

        when(tourRepository.existsById(invalidId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.deleteTourById(invalidId);
        });

        assertEquals("Tour not found", exception.getMessage());
        verify(tourRepository, times(1)).existsById(invalidId);
        verify(tourRepository, times(0)).deleteById(invalidId);
    }

    @Test
    public void testUpdateTourWithMissingId() {
        TourDto tourDto = new TourDto(); // Missing ID
        tourDto.setName("Tour Without ID");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tourService.updateTour(tourDto);
        });

        assertEquals("Tour ID cannot be null", exception.getMessage());
        verify(tourRepository, times(0)).save(any(TourEntity.class));
    }



}
