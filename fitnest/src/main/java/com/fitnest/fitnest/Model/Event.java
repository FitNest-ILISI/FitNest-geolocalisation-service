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

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String locationName;

    // Geolocation field for storing coordinates
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    private static final GeometryFactory geometryFactory = new GeometryFactory(); // Add the GeometryFactory

    @ManyToOne
    @JoinColumn(name = "sport_category_id")
    private SportCategory sportCategory; // Add SportCategory field

    // Default constructor
    public Event() {
    }

    // Constructor with parameters
    public Event(String name, String description, LocalDateTime startDate, LocalDateTime endDate,
                 String locationName, Point location, SportCategory sportCategory) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationName = locationName;
        this.location = location;
        this.sportCategory = sportCategory; // Initialize sportCategory
    }


    // Convert Event to EventDto
    public EventDto toDto() {
        EventDto dto = new EventDto();
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setLocation(new LocationDto()); // Initialize LocationDto
        dto.getLocation().setLatitude(this.location.getY()); // Set latitude
        dto.getLocation().setLongitude(this.location.getX()); // Set longitude
        dto.setLocationName(this.locationName);
        dto.setStartDate(this.startDate);
        dto.setEndDate(this.endDate);
        dto.setSportCategory(this.sportCategory != null ? this.sportCategory.getName() : null); // Convert SportCategory to String
        return dto;
    }

    // Method to create a point from latitude and longitude
    public void createLocation(double latitude, double longitude) {
        this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }
}
