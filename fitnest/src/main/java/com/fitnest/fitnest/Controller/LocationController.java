package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Dto.LocationDto;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Create a new location
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody LocationDto locationDTO) {
        Location location = new Location();
        location.setLocationName(locationDTO.getLocationName());
        location.setCoordinates(locationDTO.getLatitude(), locationDTO.getLongitude());
        Location savedLocation = locationService.saveLocation(location);
        return ResponseEntity.ok(savedLocation);
    }

    // Create multiple locations
    @PostMapping("/multiple")
    public ResponseEntity<List<Location>> createMultipleLocations(@RequestBody List<LocationDto> locationDTOs) {
        List<Location> locations = locationService.saveMultipleLocations(locationDTOs);
        return ResponseEntity.ok(locations);
    }
    @GetMapping("/getLocations")
    public ResponseEntity<List<LocationDto>> getLocations() {
        try {
            List<LocationDto> locations = locationService.getAllLocations(); // Assurez-vous que cette méthode renvoie une liste de LocationDto
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("getLocation/{id}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Long id) {
        Location location = locationService.getLocationById(id);
        if (location != null) {
            // Supposez que vous ayez une méthode pour obtenir la latitude et la longitude
            double latitude = location.getCoordinates().getX();
            double longitude = location.getCoordinates().getY();

            LocationDto locationDTO = new LocationDto(location.getLocationName(), latitude, longitude);
            return ResponseEntity.ok(locationDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
