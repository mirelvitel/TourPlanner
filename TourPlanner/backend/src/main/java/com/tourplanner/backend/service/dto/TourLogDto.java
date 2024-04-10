package com.tourplanner.backend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogDto {
    private Long id;
    private Long tourId;
    private String dateTime;
    private String comment;
    private int difficulty;
    private double totalDistance;
    private double totalTime;
    private int rating;
}


//The `TourLogDto` class serves as a Data Transfer Object (DTO) for tour log information in the Tour Planner application.
// It encapsulates the data required to create, update, and display logs of tours, including details such as the date and time of the log,
// comments, difficulty level, total distance, total time spent, and the user's rating of the tour.
// Utilizing Lombok annotations, this class simplifies the creation of boilerplate code for getters, setters, constructors, and the builder pattern.