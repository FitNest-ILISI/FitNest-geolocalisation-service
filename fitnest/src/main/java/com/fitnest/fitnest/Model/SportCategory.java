package com.fitnest.fitnest.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Data
@Entity
public class SportCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String iconPath;
    @Column(columnDefinition = "boolean default false")
    private boolean requiresRoute = false;




}
