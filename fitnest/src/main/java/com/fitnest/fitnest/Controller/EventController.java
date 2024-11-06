package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Service.EventService;
import com.fitnest.fitnest.Service.LocationService;
import com.fitnest.fitnest.Service.SportCategoryService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString; // Change import statement accordingly
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.geolatte.geom.G2D;
import org.geolatte.geom.builder.DSL;


import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;


@RestController
@RequestMapping("/api/events")
public class EventController {

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private EventService eventService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SportCategoryService sportCategoryService;


    @GetMapping("/getAllEvents")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventDto> eventDtos = events.stream()
                .map(event -> {
                    EventDto dto = event.toDto();

                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }
    @PostMapping("/create")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        // Vérifiez que cityName est bien présent
        if (eventDto.getCityName() == null || eventDto.getCityName().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Récupération de la catégorie sportive
        SportCategory sportCategory = sportCategoryService.getCategoryById(eventDto.getSportCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sport category not found"));

        // Création de l'événement
        Event event = new Event();
        event.setName(eventDto.getName());
        event.setDescription(eventDto.getDescription());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setStartTime(eventDto.getStartTime());
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setCurrentNumParticipants(0); // Initialisation à 0
        event.setImagePath(eventDto.getImagePath());
        event.setSportCategory(sportCategory);
        event.setCityName(eventDto.getCityName()); // Vérification de la valeur ici

        LineString route  = toLineString(geometryFactory, eventDto.getRouteCoordinates());
        route.setSRID(4326);

        if (eventDto.getLatitude() != 0 && eventDto.getLongitude() != 0) {
            Point location = geometryFactory.createPoint(new Coordinate(eventDto.getLongitude(), eventDto.getLatitude()));
            location.setSRID(4326);
            event.setLocation(location);
        }
        event.setChemin(route);

        // Sauvegarde de l'événement
        Event savedEvent = eventService.saveEvent(event);

        // Retour de l'événement sauvegardé en DTO
        EventDto savedEventDto = savedEvent.toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEventDto);
    }

    public LineString toLineString(GeometryFactory geometryFactory, List<double[]> coordinates) {
        List<Coordinate> coordList = new ArrayList<>();
        for (double[] coord : coordinates) {
            coordList.add(new Coordinate(coord[1], coord[0])); // Longitude, Latitude
        }
        Coordinate[] coordsArray = coordList.toArray(new Coordinate[0]);
        return geometryFactory.createLineString(coordsArray);
    }

    @PostMapping("/createMultiple")
    public ResponseEntity<List<EventDto>> createMultipleEvents(@RequestBody List<EventDto> eventRequests) {
        // Check if the list of events is empty
        if (eventRequests == null || eventRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Handle empty list case
        }

        // Save the created events in the database
        List<Event> savedEvents = eventService.saveMultipleEvents(eventRequests);

        // Convert saved events to DTOs
        List<EventDto> createdEventDtos = savedEvents.stream()
                .map(Event::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(createdEventDtos);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<EventDto>> getEventsBySportCategory(@PathVariable("categoryName") String categoryName) {
        List<Event> events = eventService.getEventsBySportCategory(categoryName);
        List<EventDto> eventDtos = events.stream().map(Event::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }


    @GetMapping("/nearby")
    public ResponseEntity<List<EventDto>> getNearbyEvents(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("distance") double distance
    ) {
        List<Event> nearbyEvents = eventService.getNearbyEvents(latitude, longitude, distance);
        List<EventDto> nearbyEventDtos = nearbyEvents.stream()
                .map(Event::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nearbyEventDtos);
    }

    @GetMapping("/between")
    public List<EventDto> getEventsBetweenDates(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            // Use LocalDate instead of LocalDateTime
            LocalDate startDateTime = LocalDate.parse(startDate);
            LocalDate endDateTime = LocalDate.parse(endDate);

            // Call the service method with LocalDate
            List<Event> events = eventService.getEventsBetweenDates(startDateTime, endDateTime);

            // Map events to DTOs
            return events.stream().map(Event::toDto).collect(Collectors.toList());
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format", e);
        }
    }

    @GetMapping("/filterByDate")
    public ResponseEntity<List<EventDto>> getEventsByDateFilter(@RequestParam("filter") String filter) {
        List<Event> events;

        switch (filter.toLowerCase()) {
            case "today":
                events = eventService.getEventsForToday();
                break;
            case "tomorrow":
                events = eventService.getEventsForTomorrow();
                break;
            case "thisweek":
                events = eventService.getEventsThisWeek();
                break;
            case "afterthisweek":
                events = eventService.getEventsAfterThisWeek();
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        List<EventDto> eventDtos = events.stream().map(Event::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "path/to/upload/dir"; // Define the path where images will be stored
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        Path path = Paths.get(uploadDir, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        String imagePath = "/uploads/" + fileName; // Modify according to your setup
        return ResponseEntity.ok(imagePath);
    }
    @GetMapping("/filterByCategoryAndDate")
    public ResponseEntity<List<EventDto>> getEventsByCategoryAndDateFilter(
            @RequestParam("categoryName") String categoryName,
            @RequestParam("filter") String filter) {

        // Filtrer les événements par catégorie
        List<Event> eventsByCategory = eventService.getEventsBySportCategory(categoryName);
        List<Event> filteredEvents;

        // Filtrer par date selon le paramètre "filter"
        switch (filter.toLowerCase()) {
            case "today":
                filteredEvents = eventService.getEventsForToday().stream()
                        .filter(eventsByCategory::contains)
                        .collect(Collectors.toList());
                break;
            case "tomorrow":
                filteredEvents = eventService.getEventsForTomorrow().stream()
                        .filter(eventsByCategory::contains)
                        .collect(Collectors.toList());
                break;
            case "thisweek":
                filteredEvents = eventService.getEventsThisWeek().stream()
                        .filter(eventsByCategory::contains)
                        .collect(Collectors.toList());
                break;
            case "afterthisweek":
                filteredEvents = eventService.getEventsAfterThisWeek().stream()
                        .filter(eventsByCategory::contains)
                        .collect(Collectors.toList());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        // Convertir les événements filtrés en DTOs et renvoyer la réponse
        List<EventDto> eventDtos = filteredEvents.stream()
                .map(Event::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }


}
