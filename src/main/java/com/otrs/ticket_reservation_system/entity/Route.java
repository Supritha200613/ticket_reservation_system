package com.otrs.ticket_reservation_system.entity;

import com.otrs.ticket_reservation_system.entity.enums.TransportType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal baseFare;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;

    private boolean active = true;

    // ── Getters & Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public BigDecimal getBaseFare() { return baseFare; }
    public BigDecimal getDistanceKm() { return distanceKm; }
    public TransportType getTransportType() { return transportType; }
    public boolean isActive() { return active; }

    public void setSource(String v) { this.source = v; }
    public void setDestination(String v) { this.destination = v; }
    public void setBaseFare(BigDecimal v) { this.baseFare = v; }
    public void setDistanceKm(BigDecimal v) { this.distanceKm = v; }
    public void setTransportType(TransportType v) { this.transportType = v; }
    public void setActive(boolean v) { this.active = v; }
}
