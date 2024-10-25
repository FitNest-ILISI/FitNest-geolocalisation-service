package com.fitnest.fitnest.Dto;

import java.time.LocalDateTime;

public class EventDto {
    private String name;
    private String description;
    private LocationDto location;
    private String locationName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String sportCategory; // Représente le nom de la catégorie de sport

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getSportCategory() {
        return sportCategory; // Retourne le nom de la catégorie de sport
    }

    public void setSportCategory(String sportCategory) {
        this.sportCategory = sportCategory; // Définit le nom de la catégorie de sport
    }
}
