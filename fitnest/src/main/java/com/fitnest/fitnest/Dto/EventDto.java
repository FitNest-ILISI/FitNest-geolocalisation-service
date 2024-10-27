package com.fitnest.fitnest.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDto {
    private String name;
    private String description;
    private LocationDto location;
    private String locationName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sportCategory; // Représente le nom de la catégorie de sport


}
