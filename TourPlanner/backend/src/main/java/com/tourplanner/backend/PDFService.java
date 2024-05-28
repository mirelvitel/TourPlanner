package com.tourplanner.backend;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.tourplanner.backend.service.dto.TourDto;
import com.tourplanner.backend.service.dto.TourLogDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDFService {

    public ByteArrayInputStream generateTourReport(TourDto tourDto) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("Tour Report")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Tour details
            document.add(new Paragraph("Tour Details")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            document.add(new Paragraph("Name: " + tourDto.getName()));
            document.add(new Paragraph("Distance: " + tourDto.getTourDistance() + " km"));
            document.add(new Paragraph("Description: " + tourDto.getTourDescription()));
            document.add(new Paragraph("Start Location: " + tourDto.getStartLocation()));
            document.add(new Paragraph("End Location: " + tourDto.getEndLocation()));
            document.add(new Paragraph("Transport Type: " + tourDto.getTransportType()));
            document.add(new Paragraph("Popularity: " + tourDto.getPopularity()));
            document.add(new Paragraph("Child Friendliness: " + tourDto.getChildFriendliness()));

            // Tour logs
            document.add(new Paragraph("Tour Logs")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(14)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 2, 3, 1, 2, 2, 1}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(20);

            // Adding header cells
            addTableHeader(table, "ID");
            addTableHeader(table, "Tour ID");
            addTableHeader(table, "Date/Time");
            addTableHeader(table, "Comment");
            addTableHeader(table, "Difficulty");
            addTableHeader(table, "Total Distance");
            addTableHeader(table, "Total Time");
            addTableHeader(table, "Rating");

            // Adding rows
            List<TourLogDto> tourLogs = tourDto.getTourLogs();
            for (TourLogDto log : tourLogs) {
                addTableCell(table, String.valueOf(log.getId()));
                addTableCell(table, String.valueOf(log.getTourId()));
                addTableCell(table, log.getDateTime());
                addTableCell(table, log.getComment());
                addTableCell(table, String.valueOf(log.getDifficulty()));
                addTableCell(table, String.valueOf(log.getTotalDistance()));
                addTableCell(table, String.valueOf(log.getTotalTime()));
                addTableCell(table, String.valueOf(log.getRating()));
            }
            document.add(table);

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void addTableHeader(Table table, String text) {
        Cell header = new Cell();
        try {
            header.add(new Paragraph(text).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10));
        } catch (IOException e) {
            e.printStackTrace();
        }
        header.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        header.setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(header);
    }

    private void addTableCell(Table table, String text) {
        Cell cell = new Cell();
        try {
            cell.add(new Paragraph(text).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(10));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cell.setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);
    }

    public ByteArrayInputStream generateSummaryReport(List<TourDto> tours) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("Summary Report")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Table headers
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(20);

            addTableHeader(table, "Tour Name");
            addTableHeader(table, "Average Time");
            addTableHeader(table, "Average Distance");
            addTableHeader(table, "Average Rating");

            // Table content
            for (TourDto tour : tours) {
                double avgTime = tour.getTourLogs().stream().mapToDouble(TourLogDto::getTotalTime).average().orElse(0);
                double avgDistance = tour.getTourLogs().stream().mapToDouble(TourLogDto::getTotalDistance).average().orElse(0);
                double avgRating = tour.getTourLogs().stream().mapToDouble(TourLogDto::getRating).average().orElse(0);

                addTableCell(table, tour.getName());
                addTableCell(table, String.format("%.2f", avgTime));
                addTableCell(table, String.format("%.2f", avgDistance));
                addTableCell(table, String.format("%.2f", avgRating));
            }
            document.add(table);

            document.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
