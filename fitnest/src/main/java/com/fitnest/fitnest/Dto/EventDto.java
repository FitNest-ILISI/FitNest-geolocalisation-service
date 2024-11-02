package com.fitnest.fitnest.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private int maxParticipants;
    private int currentNumParticipants;
    private String imagePath;
    private Long sportCategoryId;
    private String sportCategoryName;
    private Long locationId; // ID de la localisation
    private String locationName; // Nom de la localisation
    private double latitude; // Latitude de la localisation
    private double longitude; // Longitude de la localisation
}
