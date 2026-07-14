package com.otrs.ticket_reservation_system.entity;

import com.otrs.ticket_reservation_system.entity.enums.ScheduleStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(nullable = false)
    private String vehicleNumber;  // Bus/Train/Flight number

    @Column(nullable = false)
    private String operatorName;   // Operator or airline name

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int availableSeats;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.ACTIVE;

    // ── Getters & Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public Route getRoute() { return route; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getOperatorName() { return operatorName; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public ScheduleStatus getStatus() { return status; }

    public void setRoute(Route v) { this.route = v; }
    public void setVehicleNumber(String v) { this.vehicleNumber = v; }
    public void setOperatorName(String v) { this.operatorName = v; }
    public void setDepartureTime(LocalDateTime v) { this.departureTime = v; }
    public void setArrivalTime(LocalDateTime v) { this.arrivalTime = v; }
    public void setTotalSeats(int v) { this.totalSeats = v; }
    public void setAvailableSeats(int v) { this.availableSeats = v; }
    public void setStatus(ScheduleStatus v) { this.status = v; }
}
