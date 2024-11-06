package com.fitnest.fitnest.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Service.LocalTimeDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private int maxParticipants;
    private int currentNumParticipants;

    @JsonFormat(pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime startTime;

    @Column(columnDefinition = "TEXT")
    private String imagePath;

    // Champ pour stocker la localisation sous forme de point géographique
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    // Nouveau champ pour le nom de la ville
    private String cityName;

    @ManyToOne
    @JoinColumn(name = "sport_category_id")
    private SportCategory sportCategory;

    @Column(name = "chemin", columnDefinition = "geometry(LineString, 4326)")
    private LineString chemin;

    public Event(String name, String description, LocalDate startDate, LocalDate endDate,
                 LocalTime startTime, Point location, String cityName, SportCategory sportCategory,
                 String imagePath, LineString chemin) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;

        // Assurez-vous que le Point a bien le bon SRID (4326)
        if (location != null) {
            location.setSRID(4326);
        }
        this.location = location;

        this.cityName = cityName;
        this.sportCategory = sportCategory;
        this.imagePath = imagePath;

        // Assurez-vous que le LineString a bien le bon SRID (4326)
        if (chemin != null) {
            chemin.setSRID(4326);
        }
        this.chemin = chemin;
    }


    public EventDto toDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(this.id);
        eventDto.setName(this.name);
        eventDto.setDescription(this.description);
        eventDto.setStartDate(this.startDate);
        eventDto.setEndDate(this.endDate);
        eventDto.setStartTime(this.startTime);
        eventDto.setMaxParticipants(this.maxParticipants);
        eventDto.setCurrentNumParticipants(this.currentNumParticipants);
        eventDto.setImagePath(this.imagePath);
        eventDto.setSportCategoryId(this.sportCategory.getId());
        eventDto.setSportCategoryName(this.sportCategory.getName());
        eventDto.setCityName(this.cityName);
        eventDto.setLatitude(this.location.getY()); // Latitude
        eventDto.setLongitude(this.location.getX()); // Longitude

        if (this.chemin != null) {
            List<double[]> coordinates = new ArrayList<>();
            for (Coordinate coordinate : this.chemin.getCoordinates()) {
                coordinates.add(new double[]{coordinate.y, coordinate.x}); // [latitude, longitude]
            }
            eventDto.setRouteCoordinates(coordinates);
        }

        return eventDto;
    }
}
