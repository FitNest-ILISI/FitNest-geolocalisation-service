package com.fitnest.fitnest.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitnest.fitnest.Dto.EventDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private int maxParticipants;
    private int currentNumParticipants;
    private LocalTime startTime;

    private String imagePath;

    // Relation avec Location (modifié à ManyToOne)
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @JsonIgnore
    private Location location;

    @ManyToOne
    @JoinColumn(name = "sport_category_id")
    private SportCategory sportCategory;

    public Event() {}

    public Event(String name, String description, LocalDate startDate, LocalDate endDate,
                 LocalTime startTime,
                 Location location, SportCategory sportCategory, String imagePath) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.sportCategory = sportCategory;
        this.imagePath = imagePath;
        this.startTime=startTime;
    }


    public EventDto toDto() {
        EventDto dto = new EventDto();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setStartDate(this.startDate);
        dto.setEndDate(this.endDate);
        dto.setStartTime(this.startTime);
        dto.setMaxParticipants(this.maxParticipants);
        dto.setCurrentNumParticipants(this.currentNumParticipants);
        dto.setImagePath(this.imagePath);

        // Remplir les informations de la catégorie sportive
        if (this.sportCategory != null) {
            dto.setSportCategoryName(this.sportCategory.getName());
        }

        // Remplir les informations de localisation
        if (this.location != null) {
            dto.setLocationId(this.location.getId());
            dto.setLocationName(this.location.getLocationName()); // Nom de la localisation
            dto.setLatitude(this.location.getLatitude()); // Latitude
            dto.setLongitude(this.location.getLongitude()); // Longitude
        }

        return dto;
    }


}
