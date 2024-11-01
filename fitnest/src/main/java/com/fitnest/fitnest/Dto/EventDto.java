package com.fitnest.fitnest.Dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventDto {
    private String name;
    private String description;
    private LocationDto location;
    private String locationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sportCategory;
    private int maxParticipants;
    private int currentNumParticipants;
    private String imagePath;


}
