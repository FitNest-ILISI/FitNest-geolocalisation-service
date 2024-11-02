package com.fitnest.fitnest.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Champ pour stocker les coordonnées
    @Column(columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point coordinates;

    private String locationName;

    // Factory pour créer des instances de Point
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    // Méthode pour créer un point à partir de la latitude et longitude
    public void setCoordinates(double latitude, double longitude) {
        this.coordinates = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    // Méthodes pour obtenir latitude et longitude
    public double getLatitude() {
        return this.coordinates.getY();
    }

    public double getLongitude() {
        return this.coordinates.getX();
    }
}
