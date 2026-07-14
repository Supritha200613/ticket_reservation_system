package com.otrs.ticket_reservation_system.config;

import com.otrs.ticket_reservation_system.entity.Route;
import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.User;
import com.otrs.ticket_reservation_system.entity.enums.Role;
import com.otrs.ticket_reservation_system.entity.enums.ScheduleStatus;
import com.otrs.ticket_reservation_system.entity.enums.TransportType;
import com.otrs.ticket_reservation_system.repository.RouteRepository;
import com.otrs.ticket_reservation_system.repository.ScheduleRepository;
import com.otrs.ticket_reservation_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Seeds the database with sample data on first run so the app is immediately
 * testable (search, booking, login) without manual data entry.
 * Only runs if the database is empty — safe to restart the app repeatedly.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    public DataSeeder(UserRepository userRepository,
                      RouteRepository routeRepository,
                      ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedRoutesAndSchedules();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return; // already seeded

        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setEmail("admin@ticketease.com");
        admin.setPassword("admin123");
        admin.setRole(Role.ROLE_ADMIN);
        admin.setPhone("9999999999");
        userRepository.save(admin);

        User agent = new User();
        agent.setFullName("Counter Agent");
        agent.setEmail("agent@ticketease.com");
        agent.setPassword("agent123");
        agent.setRole(Role.ROLE_AGENT);
        agent.setPhone("8888888888");
        userRepository.save(agent);

        User customer = new User();
        customer.setFullName("Test Customer");
        customer.setEmail("customer@ticketease.com");
        customer.setPassword("customer123");
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setPhone("7777777777");
        userRepository.save(customer);

        System.out.println("===========================================");
        System.out.println("  SAMPLE LOGIN CREDENTIALS (auto-seeded)");
        System.out.println("  Admin:    admin@ticketease.com / admin123");
        System.out.println("  Agent:    agent@ticketease.com / agent123");
        System.out.println("  Customer: customer@ticketease.com / customer123");
        System.out.println("===========================================");
    }

    private void seedRoutesAndSchedules() {
        if (routeRepository.count() > 0) return; // already seeded

        // ── Route 1: Hyderabad → Vijayawada (Bus) ──────────────────────────
        Route r1 = new Route();
        r1.setSource("Hyderabad");
        r1.setDestination("Vijayawada");
        r1.setTransportType(TransportType.BUS);
        r1.setBaseFare(new BigDecimal("550.00"));
        r1.setDistanceKm(new BigDecimal("275.00"));
        r1.setActive(true);
        routeRepository.save(r1);

        Schedule s1 = new Schedule();
        s1.setRoute(r1);
        s1.setOperatorName("APSRTC Express");
        s1.setVehicleNumber("AP39T1234");
        s1.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0));
        s1.setArrivalTime(LocalDateTime.now().plusDays(1).withHour(13).withMinute(30));
        s1.setTotalSeats(40);
        s1.setAvailableSeats(40);
        s1.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s1);

        Schedule s2 = new Schedule();
        s2.setRoute(r1);
        s2.setOperatorName("Orange Travels");
        s2.setVehicleNumber("AP39T5678");
        s2.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(22).withMinute(0));
        s2.setArrivalTime(LocalDateTime.now().plusDays(2).withHour(3).withMinute(30));
        s2.setTotalSeats(36);
        s2.setAvailableSeats(36);
        s2.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s2);

        // ── Route 2: Hyderabad → Bangalore (Bus) ───────────────────────────
        Route r2 = new Route();
        r2.setSource("Hyderabad");
        r2.setDestination("Bangalore");
        r2.setTransportType(TransportType.BUS);
        r2.setBaseFare(new BigDecimal("950.00"));
        r2.setDistanceKm(new BigDecimal("570.00"));
        r2.setActive(true);
        routeRepository.save(r2);

        Schedule s3 = new Schedule();
        s3.setRoute(r2);
        s3.setOperatorName("VRL Travels");
        s3.setVehicleNumber("KA01F4321");
        s3.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(21).withMinute(0));
        s3.setArrivalTime(LocalDateTime.now().plusDays(3).withHour(7).withMinute(0));
        s3.setTotalSeats(45);
        s3.setAvailableSeats(45);
        s3.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s3);

        // ── Route 3: Delhi → Mumbai (Train) ────────────────────────────────
        Route r3 = new Route();
        r3.setSource("Delhi");
        r3.setDestination("Mumbai");
        r3.setTransportType(TransportType.TRAIN);
        r3.setBaseFare(new BigDecimal("1200.00"));
        r3.setDistanceKm(new BigDecimal("1400.00"));
        r3.setActive(true);
        routeRepository.save(r3);

        Schedule s4 = new Schedule();
        s4.setRoute(r3);
        s4.setOperatorName("Indian Railways");
        s4.setVehicleNumber("12951-Rajdhani");
        s4.setDepartureTime(LocalDateTime.now().plusDays(3).withHour(16).withMinute(25));
        s4.setArrivalTime(LocalDateTime.now().plusDays(4).withHour(8).withMinute(15));
        s4.setTotalSeats(72);
        s4.setAvailableSeats(72);
        s4.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s4);

        // ── Route 4: Hyderabad → Chennai (Flight) ──────────────────────────
        Route r4 = new Route();
        r4.setSource("Hyderabad");
        r4.setDestination("Chennai");
        r4.setTransportType(TransportType.FLIGHT);
        r4.setBaseFare(new BigDecimal("3500.00"));
        r4.setDistanceKm(new BigDecimal("625.00"));
        r4.setActive(true);
        routeRepository.save(r4);

        Schedule s5 = new Schedule();
        s5.setRoute(r4);
        s5.setOperatorName("IndiGo");
        s5.setVehicleNumber("6E-204");
        s5.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(15));
        s5.setArrivalTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(30));
        s5.setTotalSeats(180);
        s5.setAvailableSeats(180);
        s5.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s5);

        // ── Route 5: Mumbai → Goa (Bus) ─────────────────────────────────────
        Route r5 = new Route();
        r5.setSource("Mumbai");
        r5.setDestination("Goa");
        r5.setTransportType(TransportType.BUS);
        r5.setBaseFare(new BigDecimal("800.00"));
        r5.setDistanceKm(new BigDecimal("590.00"));
        r5.setActive(true);
        routeRepository.save(r5);

        Schedule s6 = new Schedule();
        s6.setRoute(r5);
        s6.setOperatorName("Neeta Travels");
        s6.setVehicleNumber("MH01N9988");
        s6.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(20).withMinute(30));
        s6.setArrivalTime(LocalDateTime.now().plusDays(3).withHour(6).withMinute(0));
        s6.setTotalSeats(32);
        s6.setAvailableSeats(32);
        s6.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s6);

        // ── Extra schedule on Hyderabad -> Vijayawada to show multiple operators ──
        Schedule s7 = new Schedule();
        s7.setRoute(r1);
        s7.setOperatorName("RedBus Premium");
        s7.setVehicleNumber("AP39T7777");
        s7.setDepartureTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
        s7.setArrivalTime(LocalDateTime.now().plusDays(1).withHour(19).withMinute(30));
        s7.setTotalSeats(42);
        s7.setAvailableSeats(3); // low availability to demo the "medium" badge colour
        s7.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s7);

        Schedule s8 = new Schedule();
        s8.setRoute(r2);
        s8.setOperatorName("AbhiBus Sleeper");
        s8.setVehicleNumber("KA01F9999");
        s8.setDepartureTime(LocalDateTime.now().plusDays(2).withHour(22).withMinute(30));
        s8.setArrivalTime(LocalDateTime.now().plusDays(3).withHour(8).withMinute(0));
        s8.setTotalSeats(38);
        s8.setAvailableSeats(0); // sold out, to demo the "low/sold out" badge
        s8.setStatus(ScheduleStatus.ACTIVE);
        scheduleRepository.save(s8);

        System.out.println("===========================================");
        System.out.println("  SAMPLE DATA SEEDED: 5 routes, 8 schedules");
        System.out.println("  Try searching: Hyderabad -> Vijayawada");
        System.out.println("  Or: Hyderabad -> Bangalore / Chennai");
        System.out.println("  Or: Delhi -> Mumbai / Mumbai -> Goa");
        System.out.println("===========================================");
    }
}
