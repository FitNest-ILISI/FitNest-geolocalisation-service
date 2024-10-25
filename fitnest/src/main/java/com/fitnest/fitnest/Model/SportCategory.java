package com.fitnest.fitnest.Model;

import jakarta.persistence.*;

@Entity
public class SportCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String iconPath;


    // Default constructor
    public SportCategory() {
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    // Constructor with parameters
    public SportCategory(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }


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
}
