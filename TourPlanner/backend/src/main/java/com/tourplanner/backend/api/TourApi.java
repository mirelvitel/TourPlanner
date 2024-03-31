package com.tourplanner.backend.api;

import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "tour")
public class TourApi {

    @Autowired
    private TourService tourService;

    @GetMapping
    public List<TourDto> getAllTours() {
        return tourService.getAllTours();
    }

    @GetMapping(params = "id")
    public TourDto getTourById(@RequestParam Long id) {
        return tourService.getTourById(id);
    }

    @DeleteMapping(params = "id")
    public void deleteTourById(@RequestParam Long id) {
        tourService.deleteTourById(id);
    }

    @PutMapping
    public void updateTour(@RequestBody TourDto tour) {
        tourService.updateTour(tour);
    }

    @PostMapping
    public void addNewTour(@RequestBody TourDto tour) {
        tourService.addNewTour(tour);
    }
}

