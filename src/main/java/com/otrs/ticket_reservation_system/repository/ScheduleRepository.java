package com.otrs.ticket_reservation_system.repository;

import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStatus(ScheduleStatus status);

    // Case-insensitive, partial match search — finds "Hyderabad" when user types "hyder"
    // Only returns schedules with available seats remaining
    @Query("SELECT s FROM Schedule s WHERE " +
           "LOWER(s.route.source) LIKE LOWER(CONCAT('%', :source, '%')) " +
           "AND LOWER(s.route.destination) LIKE LOWER(CONCAT('%', :destination, '%')) " +
           "AND s.status = :status " +
           "ORDER BY s.departureTime ASC")
    List<Schedule> findAvailableSchedules(@Param("source") String source,
                                          @Param("destination") String destination,
                                          @Param("status") ScheduleStatus status);

    List<Schedule> findByRouteId(Long routeId);

    // All active schedules sorted by soonest departure first
    List<Schedule> findByStatusOrderByDepartureTimeAsc(ScheduleStatus status);
}
