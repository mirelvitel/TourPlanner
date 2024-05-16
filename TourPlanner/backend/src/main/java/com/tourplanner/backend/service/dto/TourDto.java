package com.tourplanner.backend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourDto {
    private Long id;
    private String name;
    private Long tourDistance;
    private String tourDescription;
    private String transportType;
    private String endLocation;
    private String startLocation;
    private List<TourLogDto> tourLogs;
}
