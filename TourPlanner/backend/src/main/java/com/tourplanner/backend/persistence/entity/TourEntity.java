package com.tourplanner.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tour")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tour_distance")
    private Long tourDistance;

    @Column(name = "tour_description")
    private String tourDescription;

    @Column(name = "transport_type")
    private String transportType;

    @Column(name = "end_location")
    private String endLocation;

    @Column(name = "start_location")
    private String startLocation;

    @OneToMany(mappedBy = "tour")
    private List<TourLogEntity> tourLogs;
}
