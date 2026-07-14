package com.otrs.ticket_reservation_system.entity;

import com.otrs.ticket_reservation_system.entity.enums.BookingStatus;
import com.otrs.ticket_reservation_system.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String pnr; // Unique booking reference

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Passenger who booked

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // The trip booked

    @Column(nullable = false)
    private int seatsBooked;

    @Column(nullable = false)
    private String seatNumbers; // e.g. "A1,A2"

    @Column(nullable = false)
    private String passengerName;

    @Column(nullable = false)
    private int passengerAge;

    @Column(nullable = false)
    private String passengerGender;

    @Column(nullable = false)
    private String idType; // AADHAAR, PASSPORT, DL

    @Column(nullable = false)
    private String idNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    private LocalDateTime bookingDate = LocalDateTime.now();

    // ── Payment details (demo payment gateway) ────────────────────────
    private String paymentMethod;     // UPI, CARD, NETBANKING, WALLET
    private String transactionId;     // demo gateway transaction reference
    private LocalDateTime paymentDate;

    // ── Getters & Setters ───────────────────────────────────────────
    public Long getId() { return id; }
    public String getPnr() { return pnr; }
    public User getUser() { return user; }
    public Schedule getSchedule() { return schedule; }
    public int getSeatsBooked() { return seatsBooked; }
    public String getSeatNumbers() { return seatNumbers; }
    public String getPassengerName() { return passengerName; }
    public int getPassengerAge() { return passengerAge; }
    public String getPassengerGender() { return passengerGender; }
    public String getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getBookingDate() { return bookingDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }

    public void setPnr(String v) { this.pnr = v; }
    public void setUser(User v) { this.user = v; }
    public void setSchedule(Schedule v) { this.schedule = v; }
    public void setSeatsBooked(int v) { this.seatsBooked = v; }
    public void setSeatNumbers(String v) { this.seatNumbers = v; }
    public void setPassengerName(String v) { this.passengerName = v; }
    public void setPassengerAge(int v) { this.passengerAge = v; }
    public void setPassengerGender(String v) { this.passengerGender = v; }
    public void setIdType(String v) { this.idType = v; }
    public void setIdNumber(String v) { this.idNumber = v; }
    public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public void setStatus(BookingStatus v) { this.status = v; }
    public void setPaymentStatus(PaymentStatus v) { this.paymentStatus = v; }
    public void setBookingDate(LocalDateTime v) { this.bookingDate = v; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public void setTransactionId(String v) { this.transactionId = v; }
    public void setPaymentDate(LocalDateTime v) { this.paymentDate = v; }
}
