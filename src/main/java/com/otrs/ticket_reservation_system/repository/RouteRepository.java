package com.otrs.ticket_reservation_system.repository;

import com.otrs.ticket_reservation_system.entity.Route;
import com.otrs.ticket_reservation_system.entity.enums.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByActiveTrue();
    List<Route> findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCase(String source, String destination);
    List<Route> findByTransportType(TransportType type);
}
