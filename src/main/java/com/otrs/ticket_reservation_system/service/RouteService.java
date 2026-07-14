package com.otrs.ticket_reservation_system.service;

import com.otrs.ticket_reservation_system.entity.Route;
import com.otrs.ticket_reservation_system.repository.RouteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> getAllActiveRoutes() {
        return routeRepository.findByActiveTrue();
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    public Route addRoute(Route route) {
        return routeRepository.save(route);
    }

    public Route updateRoute(Route route) {
        return routeRepository.save(route);
    }

    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    public List<Route> searchRoutes(String source, String destination) {
        return routeRepository.findBySourceContainingIgnoreCaseAndDestinationContainingIgnoreCase(source, destination);
    }
}
