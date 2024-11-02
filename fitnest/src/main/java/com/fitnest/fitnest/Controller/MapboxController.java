package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Service.MapboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mapbox")
public class MapboxController {

    @Autowired
    private MapboxService mapboxService;

    @GetMapping("/distance")
    public ResponseEntity<Double> getDistance(
            @RequestParam double longitude1,
            @RequestParam double latitude1,
            @RequestParam double longitude2,
            @RequestParam double latitude2) {

        double distance = mapboxService.getDistanceBetweenPoints(longitude1, latitude1, longitude2, latitude2);
        return ResponseEntity.ok(distance);
    }
}
