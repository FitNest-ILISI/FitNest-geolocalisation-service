package com.fitnest.fitnest.Controller;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Service.EventService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    @GetMapping("/getAllEvents")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventDto> eventDtos = events.stream().map(Event::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(eventDtos);
    }
    @PostMapping("/create")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto) {
        if (eventDto.getLocation() == null || eventDto.getName() == null) {
            return ResponseEntity.badRequest().body(null); // Informative response could be better
        }

        // Find the sport category by name
        Optional<SportCategory> sportCategoryOpt = eventService.getSportCategoryByName(eventDto.getSportCategory());
        if (sportCategoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Point location = geometryFactory.createPoint(
                new Coordinate(eventDto.getLocation().getLongitude(), eventDto.getLocation().getLatitude())
        );

        // Create a new event
        Event event = new Event(
                eventDto.getName(),
                eventDto.getDescription(),
                eventDto.getStartDate(),
                eventDto.getEndDate(),
                eventDto.getLocationName(),
                location,
                sportCategoryOpt.get(),
                eventDto.getImagePath()
        );

        // Set participants
        event.setMaxParticipants(eventDto.getMaxParticipants());
        event.setCurrentNumParticipants(eventDto.getCurrentNumParticipants());

        try {
            Event createdEvent = eventService.saveEvent(event);
            EventDto createdEventDto = createdEvent.toDto();
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEventDto);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/createMultiple")
    public ResponseEntity<List<EventDto>> createMultipleEvents(@RequestBody List<EventDto> eventRequests) {
        // Vérifier si la liste d'événements est vide
        if (eventRequests == null || eventRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Gérer les cas où la liste est vide
        }

        // Sauvegarder les événements créés dans la base de données
        List<Event> savedEvents = eventService.saveMultipleEvents(eventRequests);

        // Convertir les événements sauvegardés en DTO
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
        LocalDateTime startDateTime = LocalDateTime.parse(startDate);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate);

       List <Event> events= eventService.getEventsBetweenDates(startDateTime,endDateTime);
       List<EventDto> eventDtos = events.stream().map(Event::toDto).collect(Collectors.toList());
       return eventDtos;
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
                events = eventService.getEventsForThisWeek();
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
