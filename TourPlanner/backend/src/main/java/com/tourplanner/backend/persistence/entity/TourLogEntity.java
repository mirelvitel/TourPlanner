package com.tourplanner.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tour_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private TourEntity tour;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "comment")
    private String comment;

    @Column(name = "difficulty")
    private int difficulty;

    @Column(name = "total_distance")
    private double totalDistance;

    @Column(name = "total_time")
    private double totalTime;

    @Column(name = "rating")
    private int rating;

}


//This file defines the `TourLogEntity` class,
// which is a JPA entity representing the tour log data in the database.
// It includes annotations to map each field to a corresponding column in the `tour_log` table,
// with fields for storing a tour's ID, the date and time of the log, comments, difficulty, total distance, total time, and a rating.
// The class uses Lombok annotations for boilerplate code like getters, setters, constructors, and a builder pattern for easier instantiation.
// This entity is central to persisting and accessing tour log information within the application, enabling CRUD operations on tour logs.