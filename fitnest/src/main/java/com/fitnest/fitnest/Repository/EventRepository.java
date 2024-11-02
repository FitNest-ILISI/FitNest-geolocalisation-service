package com.fitnest.fitnest.Repository;

import com.fitnest.fitnest.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        List<Event> findBySportCategoryName(String categoryName);

        @Query("SELECT e FROM Event e WHERE ST_Distance(e.location, ST_GeomFromText(:point, 4326)) <= :distance")
        List<Event> findNearbyEvents(@Param("point") String point, @Param("distance") double distance);

        List<Event> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

        // Methods for date filters
        @Query("SELECT e FROM Event e WHERE DATE(e.startDate) = CURRENT_DATE")
        List<Event> findEventsForToday();

        @Query("SELECT e FROM Event e WHERE DATE(e.startDate) = CURRENT_DATE + 1")
        List<Event> findEventsForTomorrow();

        @Query("SELECT e FROM Event e WHERE e.startDate >= CURRENT_DATE AND e.startDate < :endOfWeek")
        List<Event> findEventsForThisWeek(@Param("endOfWeek") LocalDate endOfWeek);

        // Corrected method for finding events after this week
        @Query("SELECT e FROM Event e WHERE e.startDate >= :date")
        List<Event> findEventsAfterThisWeek(@Param("date") LocalDate date);
}
