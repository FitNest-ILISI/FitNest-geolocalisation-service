package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Service.EventService;
import com.fitnest.fitnest.Service.LocationService;
import com.fitnest.fitnest.Service.SportCategoryService;
import org.locationtech.jts.geom.GeometryFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<EventDto> eventDtos = events.stream().map(Event::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }
    @PostMapping("/create")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        // Vérification des champs requis
        if (eventDto.getLocationId() == null || eventDto.getName() == null) {
            return ResponseEntity.badRequest().body(null); // Bad request si les données sont incomplètes
        }

        // Récupérer la catégorie de sport par ID
        Optional<SportCategory> sportCategory = sportCategoryService.getCategoryById(eventDto.getSportCategoryId());
        if (sportCategory.isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Bad request si la catégorie n'existe pas
        }

        // Récupérer l'objet Location par ID
        Location location = locationService.getLocationById(eventDto.getLocationId());
        if (location == null) {
            return ResponseEntity.badRequest().body(null); // Bad request si la localisation n'est pas trouvée
        }

        // Création d'un nouvel événement
        Event event = new Event(
                eventDto.getName(),
                eventDto.getDescription(),
                eventDto.getStartDate(),
                eventDto.getEndDate(),
                eventDto.getStartTime(), // Passer le startTime
                location, // Utiliser la localisation récupérée
                sportCategory.get(), // Obtenir l'objet SportCategory
                eventDto.getImagePath()
        );

        // Définir les participants
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setCurrentNumParticipants(eventDto.getCurrentNumParticipants());

        try {
            // Enregistrer l'événement
            Event createdEvent = eventService.saveEvent(event);
            EventDto createdEventDto = createdEvent.toDto(); // Convertir en DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEventDto); // Retourner l'événement créé
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erreur interne du serveur
        }
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
}
