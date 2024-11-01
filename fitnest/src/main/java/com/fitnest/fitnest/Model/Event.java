package com.fitnest.fitnest.Model;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Dto.LocationDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String locationName;
    private int maxParticipants;
    private int currentNumParticipants;
    private LocalTime startTime;


    // Field to store the image path
    private String imagePath;

    // Geolocation field for storing coordinates
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    @ManyToOne
    @JoinColumn(name = "sport_category_id")
    private SportCategory sportCategory;

    // Default constructor
    public Event() {
    }

    // Constructor with parameters
    public Event(String name, String description, LocalDate startDate, LocalDate endDate,
                 String locationName, Point location, SportCategory sportCategory, String imagePath) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationName = locationName;
        this.location = location;
        this.sportCategory = sportCategory;
        this.imagePath = imagePath; // Initialize imagePath
    }

    // Convert Event to EventDto
    public EventDto toDto() {
        EventDto dto = new EventDto();
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setLocation(new LocationDto());
        dto.getLocation().setLatitude(this.location.getY());
        dto.getLocation().setLongitude(this.location.getX());
        dto.setLocationName(this.locationName);
        dto.setStartDate(this.startDate);
        dto.setEndDate(this.endDate);
        dto.setSportCategory(this.sportCategory != null ? this.sportCategory.getName() : null);
        dto.setMaxParticipants(this.maxParticipants);
        dto.setCurrentNumParticipants(this.currentNumParticipants);
        dto.setImagePath(this.imagePath); // Add imagePath to DTO

        return dto;
    }

    // Method to create a point from latitude and longitude
    public void createLocation(double latitude, double longitude) {
        this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
