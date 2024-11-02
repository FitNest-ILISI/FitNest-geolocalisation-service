package com.fitnest.fitnest.Service;

import com.fitnest.fitnest.Dto.LocationDto;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> saveMultipleLocations(List<LocationDto> locationDTOs) {
        List<Location> locations = locationDTOs.stream().map(dto -> {
            Location location = new Location();
            location.setLocationName(dto.getLocationName());
            location.setCoordinates(dto.getLatitude(), dto.getLongitude());
            return location;
        }).collect(Collectors.toList());
        return locationRepository.saveAll(locations);
    }

    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll(); // ou votre méthode de récupération
        return locations.stream()
                .map(location -> new LocationDto(location.getLocationName(), location.getLatitude(), location.getLongitude()))
                .collect(Collectors.toList());
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElse(null);
    }
}
