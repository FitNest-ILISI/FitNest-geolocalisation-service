package com.fitnest.fitnest.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    private String cityName; // Nouveau champ pour le nom de la ville
    private double latitude;
    private double longitude;
    private List<double[]> routeCoordinates;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getCurrentNumParticipants() {
        return currentNumParticipants;
    }

    public void setCurrentNumParticipants(int currentNumParticipants) {
        this.currentNumParticipants = currentNumParticipants;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Long getSportCategoryId() {
        return sportCategoryId;
    }

    public void setSportCategoryId(Long sportCategoryId) {
        this.sportCategoryId = sportCategoryId;
    }

    public String getSportCategoryName() {
        return sportCategoryName;
    }

    public void setSportCategoryName(String sportCategoryName) {
        this.sportCategoryName = sportCategoryName;
    }

    public String getCityName() { // Getter pour le nom de la ville
        return cityName;
    }

    public void setCityName(String cityName) { // Setter pour le nom de la ville
        this.cityName = cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<double[]> getRouteCoordinates() {
        return routeCoordinates;
    }

    public void setRouteCoordinates(List<double[]> routeCoordinates) {
        this.routeCoordinates = routeCoordinates;
    }
}
