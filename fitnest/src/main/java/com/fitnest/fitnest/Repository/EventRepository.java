package com.fitnest.fitnest.Repository;

import com.fitnest.fitnest.Model.Event;
import com.fitnest.fitnest.Model.SportCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        @Query("SELECT e FROM Event e WHERE ST_Distance(e.location, ST_MakePoint(:longitude, :latitude)) <= :distance")
        List<Event> findEventsNearby(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distance") double distance);

        List<Event> findByStartDate(LocalDate date);

        List<Event> findByStartDateAfter(LocalDate date);

        List<Event> findByEndDateBefore(LocalDate date);

        List<Event> findByNameContainingIgnoreCase(String name);

        List<Event> findByLocationNameContainingIgnoreCase(String locationName);

        List<Event> findBySportCategory(SportCategory sportCategory);

        @Query("SELECT e FROM Event e WHERE e.startDate >= :startDate AND e.endDate <= :endDate")
        List<Event> findByStartDateAfterAndEndDateBefore(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

        List<Event> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

        List<Event> findByStartDateAfter(LocalDateTime start);
}


