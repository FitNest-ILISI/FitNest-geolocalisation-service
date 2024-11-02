package com.fitnest.fitnest.Service;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.Location;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Repository.EventRepository;
import com.fitnest.fitnest.Repository.SportCategoryRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private Event convertToEvent(EventDto dto) {
        // Récupérer l'objet Location en utilisant l'ID de location
        Optional<Location> locationOpt = Optional.ofNullable(locationService.getLocationById(dto.getLocationId()));

        // Vérifier si la localisation existe
        Location location = locationOpt.orElseThrow(() -> new IllegalArgumentException("Location not found"));

        // Récupérer la catégorie de sport en utilisant l'ID de la catégorie
        Optional<SportCategory> sportCategoryOpt = sportCategoryService.getCategoryById(dto.getSportCategoryId());
        SportCategory sportCategory = sportCategoryOpt.orElseThrow(() -> new IllegalArgumentException("Sport category not found"));

        // Créer un objet Event avec les informations restantes
        Event event = new Event(
                dto.getName(),
                dto.getDescription(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStartTime(),
                location,
                sportCategory, // Ajout de la catégorie de sport ici
                dto.getImagePath()
        );

        event.setMaxParticipants(dto.getMaxParticipants());
        event.setCurrentNumParticipants(dto.getCurrentNumParticipants());

        return eventRepository.save(event); // Sauvegarder l'événement
    }

    public List<Event> getEventsBySportCategoryName(String categoryName) {
        return eventRepository.findBySportCategoryName(categoryName);
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
        LocalDate oneWeekLater = LocalDate.now().plusDays(7);
        return eventRepository.findEventsAfterThisWeek(oneWeekLater);
    }
}
