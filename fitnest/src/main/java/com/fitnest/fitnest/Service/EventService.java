package com.fitnest.fitnest.Service;

import com.fitnest.fitnest.Dto.EventDto;
import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.SportCategory;
import com.fitnest.fitnest.Repository.EventRepository;
import com.fitnest.fitnest.Repository.SportCategoryRepository; // Assurez-vous d'importer ce repository
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SportCategoryRepository sportCategoryRepository; // Pour la gestion des catégories de sport

    private final GeometryFactory geometryFactory = new GeometryFactory();

    // Créer un nouvel événement
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    // Enregistrer plusieurs événements
    public List<Event> saveMultipleEvents(List<EventDto> eventRequests) {
        List<Event> savedEvents = new ArrayList<>();

        for (EventDto eventRequest : eventRequests) {
            // Créer le point avec les coordonnées
            Point location = geometryFactory.createPoint(
                    new Coordinate(eventRequest.getLocation().getLongitude(), eventRequest.getLocation().getLatitude())
            );

            // Créer un nouvel événement
            Event event = new Event();
            event.setName(eventRequest.getName());
            event.setDescription(eventRequest.getDescription());
            event.setLocation(location);
            event.setLocationName(eventRequest.getLocationName());
            event.setStartDate(eventRequest.getStartDate());
            event.setEndDate(eventRequest.getEndDate());

            // Trouver la catégorie de sport
            Optional<SportCategory> sportCategory = sportCategoryRepository.findByName(eventRequest.getSportCategory());
            sportCategory.ifPresent(event::setSportCategory); // Si la catégorie existe, l'associer à l'événement

            // Ajouter à la liste des événements enregistrés
            savedEvents.add(saveEvent(event));
        }

        return savedEvents;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getNearbyEvents(Point userLocation, double distance) {
        return eventRepository.findEventsNearby(userLocation.getY(), userLocation.getX(), distance); // Longitude, Latitude
    }


    public List<Event> getEventsBySportCategory(String categoryName) {
        Optional<SportCategory> sportCategory = sportCategoryRepository.findByName(categoryName);
        if (sportCategory.isPresent()) {
            return eventRepository.findBySportCategory(sportCategory.orElse(null));
        }
        return List.of(); // Retourne une liste vide si la catégorie n'existe pas
    }
    public Optional<SportCategory> getSportCategoryByName(String name) {
        return sportCategoryRepository.findByName(name);
    }
    public List<Event> getNearbyEvents(double latitude, double longitude, double distance) {
        return eventRepository.findEventsNearby(latitude, longitude, distance);
    }
    public List<Event> getEventsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findByStartDateAfterAndEndDateBefore(startDate, endDate);
    }
}
