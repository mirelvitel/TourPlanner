package com.tourplanner.backend.api;

import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.dto.TourDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "tour")
public class TourApi {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @Autowired
    private TourService tourService;

    @GetMapping
    public List<TourDto> getAllTours() {
        return tourService.getAllTours();
    }
    @PostMapping
    public void insertNewTour(@RequestBody TourDto tour) {
        tourService.saveNewTour(tour);
    }

}

