package com.tourplanner.backend.api;

import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/tour")
public class TourApi {

    private final TourService tourService;
    private final TourLogService tourLogService;

    @Autowired
    public TourApi(TourService tourService, TourLogService tourLogService) {
        this.tourService = tourService;
        this.tourLogService = tourLogService;
    }

    // Endpoint to retrieve all tours
    @GetMapping
    public List<TourDto> getAllTours() {
        return tourService.getAllTours();
    }

    // Endpoint to retrieve a single tour by ID
    @GetMapping("/{id}")
    public TourDto getTourById(@PathVariable Long id) {
        return tourService.getTourById(id);
    }

    // Endpoint to delete a tour by ID
    @DeleteMapping("/{id}")
    public void deleteTourById(@PathVariable Long id) {
        tourService.deleteTourById(id);
    }

    // Endpoint to update a tour
    @PutMapping("/{id}")
    public void updateTour(@PathVariable Long id, @RequestBody TourDto tourDto) {
        tourDto.setId(id);
        System.out.println("1");
        tourService.updateTour(tourDto);
        System.out.println("2");
    }

    // Endpoint to add a new tour
    @PostMapping
    public void addNewTour(@RequestBody TourDto tour) {
        tourService.addNewTour(tour);
    }

    // Tour log management endpoints

    // Endpoint to add a new tour log
    @PostMapping("/{tourId}/log")
    public void addTourLog(@PathVariable Long tourId, @RequestBody TourLogDto tourLogDto) {
        tourLogDto.setTourId(tourId); // Ensure the DTO has the correct tourId
        tourLogService.addTourLog(tourLogDto);
    }

    // Endpoint to retrieve all logs for a specific tour
    @GetMapping("/{tourId}/log")
    public List<TourLogDto> getTourLogsByTourId(@PathVariable Long tourId) {
        return tourLogService.getTourLogsByTourId(tourId);
    }

    // Endpoint to update a specific tour log
    @PutMapping("/{tourId}/log/{logId}")
    public void updateTourLog(@PathVariable Long tourId, @PathVariable Long logId, @RequestBody TourLogDto tourLogDto) {
        tourLogDto.setId(logId); // Ensure the DTO has the correct logId
        tourLogDto.setTourId(tourId); // Ensure the DTO has the correct tourId
        tourLogService.updateTourLog(tourLogDto);
    }

    // Endpoint to delete a specific tour log
    @DeleteMapping("/{tourId}/log/{logId}")
    public void deleteTourLog(@PathVariable Long logId) {
        tourLogService.deleteTourLog(logId);
    }
}
