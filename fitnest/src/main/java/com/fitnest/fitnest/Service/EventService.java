package com.fitnest.fitnest.Service;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Repository.EventRepository;
import com.fitnest.fitnest.Repository.SportCategoryRepository;
import org.geolatte.geom.G2D;
import org.geolatte.geom.builder.DSL;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString ;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SportCategoryRepository sportCategoryRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SportCategoryService sportCategoryService;

    private final GeometryFactory geometryFactory = new GeometryFactory(); // Instantiate GeometryFactory here

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<SportCategory> getSportCategoryByName(String name) {
        return sportCategoryRepository.findByName(name);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> saveMultipleEvents(List<EventDto> eventDtos) {
        return eventDtos.stream().map(this::convertToEvent).collect(Collectors.toList());
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null); // Retourne l'événement ou null si non trouvé
    }

    public LineString toLineString(List<double[]> coordinates) {
        if (coordinates.isEmpty()) {
            return null;
        }

        Coordinate[] coords = coordinates.stream()
                .map(coord -> new Coordinate(coord[0], coord[1])) // Assurez-vous que l'ordre est correct : longitude, latitude
                .toArray(Coordinate[]::new);

        LineString lineString = geometryFactory.createLineString(coords);
        lineString.setSRID(4326);

        return lineString;
    }

    private Event convertToEvent(EventDto dto) {
        // Create the Point object for location using latitude and longitude
        Point location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));

        // Fetch the sport category using the category ID
        SportCategory sportCategory = sportCategoryService.getCategoryById(dto.getSportCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Sport category not found"));

        // Create a LineString for the route
        LineString route = toLineString(dto.getRouteCoordinates());

        // Create the Event object
        Event event = new Event(
                dto.getName(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStartTime(),
                location,
                dto.getCityName(),
                sportCategory,
                dto.getImagePath(),
                route
        );

        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentNumParticipants(dto.getCurrentNumParticipants());

        return eventRepository.save(event);
    }

    public List<Event> getEventsBySportCategory(String categoryName) {
        return eventRepository.findBySportCategoryName(categoryName);
    }

    public List<Event> getNearbyEvents(double latitude, double longitude, double distance) {
        String pointWKT = String.format("POINT(%s %s)", longitude, latitude);
        return eventRepository.findNearbyEvents(pointWKT, distance);
    }

    public List<Event> getEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByStartDateBetween(startDate, endDate);
    }

    public List<Event> getEventsForToday() {
        return eventRepository.findEventsForToday();
    }

    public List<Event> getEventsForTomorrow() {
        return eventRepository.findEventsForTomorrow();
    }

    public List<Event> getEventsThisWeek() {
        LocalDate oneWeekLater = LocalDate.now().plusDays(7);
        return eventRepository.findEventsForThisWeek(oneWeekLater);
    }

    public List<Event> getEventsAfterThisWeek() {
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        return eventRepository.findEventsAfterThisWeek(nextMonday);
    }

    public List<Event> findEventsByPartOfDay(String partOfDay) {
        LocalTime startTime, endTime;
        List<Event> events = new ArrayList<>();

        switch (partOfDay.toLowerCase()) {
            case "morning":
                startTime = LocalTime.of(5, 0);
                endTime = LocalTime.of(11, 59);
                events = eventRepository.findByTimeRange(startTime, endTime);
                break;
            case "afternoon":
                startTime = LocalTime.of(12, 0);
                endTime = LocalTime.of(16, 59);
                events = eventRepository.findByTimeRange(startTime, endTime);
                break;
            case "evening":
                startTime = LocalTime.of(17, 0);
                endTime = LocalTime.of(20, 59);
                events = eventRepository.findByTimeRange(startTime, endTime);
                break;
            case "night":
                events.addAll(eventRepository.findByTimeRange(LocalTime.of(21, 0), LocalTime.of(23, 59)));
                events.addAll(eventRepository.findByTimeRange(LocalTime.of(0, 0), LocalTime.of(4, 59)));
                break;
            default:
                throw new IllegalArgumentException("Invalid part of day: " + partOfDay);
        }

        return events;
    }
}
