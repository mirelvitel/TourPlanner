package com.tourplanner.backend.api;

import com.tourplanner.backend.PDFService;
import com.tourplanner.backend.service.TourService;
import com.tourplanner.backend.service.TourLogService;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/tour")
public class TourApi {

    private final TourService tourService;
    private final TourLogService tourLogService;
    private final PDFService pdfService;

    private static final Logger logger = Logger.getLogger(TourApi.class);

    @Autowired
    public TourApi(TourService tourService, TourLogService tourLogService, PDFService pdfService) {
        this.tourService = tourService;
        this.tourLogService = tourLogService;
        this.pdfService = pdfService;
    }

    // Endpoint to retrieve all tours
    @GetMapping
    public List<TourDto> getAllTours() {
        logger.info("Request received to get all tours.");
        List<TourDto> tours = tourService.getAllTours();
        logger.info("Number of tours fetched: " + tours.size());
        return tours;
    }

    // Endpoint to retrieve a single tour by ID
    @GetMapping("/{id}")
    public TourDto getTourById(@PathVariable("id") Long id) {
        logger.info("Request received to get tour with ID: " + id);
        TourDto tour = tourService.getTourById(id);
        logger.info("Tour data fetched for ID: " + id);
        return tour;
    }

    // Endpoint to delete a tour by ID
    @DeleteMapping("/{id}")
    public void deleteTourById(@PathVariable("id") Long id) {
        logger.info("Request received to delete tour with ID: " + id);
        tourService.deleteTourById(id);
        logger.info("Tour deleted with ID: " + id);
    }

    // Endpoint to update a tour
    @PutMapping("/{id}")
    public void updateTour(@PathVariable("id") Long id, @RequestBody TourDto tourDto) {
        logger.info("Request received to update tour with ID: " + id);
        tourDto.setId(id);
        tourService.updateTour(tourDto);
        logger.info("Tour updated with ID: " + id);
    }

    // Endpoint to add a new tour
    @PostMapping
    public void addNewTour(@RequestBody TourDto tour) {
        logger.info("Request received to add a new tour.");
        tourService.addNewTour(tour);
        logger.info("New tour added: " + tour.getName());
    }

    // Endpoint to add a new tour log
    @PostMapping("/{tourId}/log")
    public void addTourLog(@PathVariable("tourId") Long tourId, @RequestBody TourLogDto tourLogDto) {
        logger.info("Request received to add a new log for tour ID: " + tourId);
        tourLogDto.setTourId(tourId);
        tourLogService.addTourLog(tourLogDto);
        logger.info("New log added for tour ID: " + tourId);
    }

    // Endpoint to retrieve all logs for a specific tour
    @GetMapping("/{tourId}/log")
    public List<TourLogDto> getTourLogsByTourId(@PathVariable("tourId") Long tourId) {
        logger.info("Request received to get logs for tour ID: " + tourId);
        List<TourLogDto> logs = tourLogService.getTourLogsByTourId(tourId);
        logger.info("Number of logs fetched for tour ID: " + tourId + " is " + logs.size());
        return logs;
    }

    // Endpoint to update a specific tour log
    @PutMapping("/{tourId}/log/{logId}")
    public void updateTourLog(@PathVariable("tourId") Long tourId, @PathVariable("logId") Long logId, @RequestBody TourLogDto tourLogDto) {
        logger.info("Request received to update log ID: " + logId + " for tour ID: " + tourId);
        tourLogDto.setId(logId); // Ensure the DTO has the correct logId
        tourLogDto.setTourId(tourId); // Ensure the DTO has the correct tourId
        tourLogService.updateTourLog(tourLogDto);
        logger.info("Log updated with ID: " + logId + " for tour ID: " + tourId);
    }

    // Endpoint to delete a specific tour log
    @DeleteMapping("/{tourId}/log/{logId}")
    public void deleteTourLog(@PathVariable("logId") Long logId) {
        logger.info("Request received to delete log with ID: " + logId);
        tourLogService.deleteTourLog(logId);
        logger.info("Log deleted with ID: " + logId);
    }

    // Endpoint to generate summary report
    @GetMapping("/summary-report")
    public ResponseEntity<byte[]> generateSummaryReport() throws IOException {
        logger.info("Request received for summary report generation.");
        List<TourDto> tours = tourService.getAllTours();
        logger.info("Number of tours fetched: " + tours.size());
        ByteArrayInputStream bis = pdfService.generateSummaryReport(tours);
        logger.info("Summary report generated successfully.");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=summary_report.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());
    }

    // Endpoint to generate tour report
    @GetMapping("/report/{id:\\d+}")
    public ResponseEntity<byte[]> generateTourReport(@PathVariable Long id) throws IOException {
        logger.info("Request received for tour report generation for ID: " + id);
        TourDto tourDto = tourService.getTourById(id);
        logger.info("Tour data fetched for ID: " + id);
        ByteArrayInputStream bis = pdfService.generateTourReport(tourDto);
        logger.info("Tour report generated successfully for ID: " + id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=tour_report.pdf");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(bis.readAllBytes());
    }
}
