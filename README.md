# Online Ticket Reservation System

A complete Spring Boot web application for reserving bus, train, and flight tickets.

## Tech Stack
- **Java 17** + **Spring Boot 4.1.0**
- **Spring Data JPA** + **Hibernate** (ORM)
- **MySQL 8** (database)
- **Thymeleaf** (server-side templates / frontend)

## Prerequisites
- Java 17+
- MySQL 8 running on localhost:3306
- Maven 3.8+

## Database Setup
```sql
CREATE DATABASE ticket_reservation_db;
```

## Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

## Run
```bash
cd otrs
mvn spring-boot:run
```
Open http://localhost:8080

## ⭐ Sample Data (Auto-Seeded on First Run)

The app automatically seeds sample data into an empty database so you can
test search and booking immediately — no manual data entry needed.

### Login Credentials
| Role     | Email                    | Password    |
|----------|---------------------------|-------------|
| Admin    | admin@ticketease.com      | admin123    |
| Agent    | agent@ticketease.com      | agent123    |
| Customer | customer@ticketease.com   | customer123 |

You can also register a new Customer/Agent account via the Register page.

### Sample Routes & Schedules (try searching these)
| From      | To          | Type   |
|-----------|-------------|--------|
| Hyderabad | Vijayawada  | Bus    |
| Hyderabad | Bangalore   | Bus    |
| Hyderabad | Chennai     | Flight |
| Delhi     | Mumbai      | Train  |
| Mumbai    | Goa         | Bus    |

Search is **case-insensitive and partial-match** — typing "hyder" will find "Hyderabad".

## How to Test the Full Flow
1. Login as `customer@ticketease.com` / `customer123`
2. On the dashboard, use the search box: From `Hyderabad`, To `Vijayawada`
3. You'll see search results styled like RedBus/AbhiBus — operator badges,
   departure/arrival times, and colour-coded seat availability
4. Click **View Seats →** on any schedule with available seats
5. Fill passenger details — enter seat numbers matching the seat count
   (e.g. if booking 2 seats, enter `A1,A2`)
6. Submit — your seats are held and you're taken to the **Demo Payment** page
7. Choose a payment method (UPI/Card/Net Banking/Wallet) and click Pay
   (no real payment is processed — this is a simulated gateway)
8. You'll see a Payment Successful screen, then auto-redirect to your e-ticket
   showing the PNR and transaction ID
9. Go to **My Bookings** to view, or cancel the booking
10. Login as `admin@ticketease.com` to see Add Route / Add Schedule / All Bookings

### Payment Gateway Notes
- This is a **demo/simulated** payment gateway for project demonstration
- No real payment provider (Razorpay, Stripe, etc.) is integrated
- Payment always "succeeds" in this demo — it generates a fake transaction ID
- If you click "Cancel Payment", the held seats are released back automatically

## Features
- User registration & login (Customer / Agent / Admin roles)
- Role-based access: only Admin/Agent can manage routes & schedules
- Search routes/schedules — case-insensitive, partial match
- **RedBus / AbhiBus style search results** — operator branding badges,
  journey timeline, colour-coded seat availability (green/orange/red)
- Book tickets with passenger details and seat number validation
- **Demo Payment Gateway** — UPI / Card / Net Banking / Wallet checkout UI
  (simulated, no real payment provider — clearly marked as a demo)
- Two-step booking flow: seats are held (PENDING) during checkout,
  then CONFIRMED only after the demo payment succeeds
- Seats automatically released if payment is cancelled/abandoned
- Auto-generate unique, collision-safe PNR
- Booking confirmation e-ticket with transaction details
- Cancel bookings with automatic seat restoration (`@Transactional`)
- PNR search (scoped to own bookings for customers)
- Admin: full CRUD on routes and schedules
- Admin: view all bookings system-wide
- Auto-seeded sample data for immediate testing (8 schedules with
  varied seat availability to demo all UI states)

## Project Structure
```
src/main/java/com/otrs/ticket_reservation_system/
├── config/          DataSeeder (auto-populates sample data on first run)
├── controller/      AuthController, RouteController, ScheduleController,
│                    BookingController, DashboardController
├── entity/          User, Route, Schedule, Booking
│   └── enums/       Role, TransportType, ScheduleStatus, BookingStatus, PaymentStatus
├── repository/      UserRepository, RouteRepository, ScheduleRepository, BookingRepository
└── service/         UserService, RouteService, ScheduleService, BookingService

src/main/resources/
├── templates/       login, register, dashboard, routes, schedules,
│                    add-route, edit-route, add-schedule, book-ticket,
│                    booking-confirmation, my-bookings, all-bookings
└── application.properties
```

## Troubleshooting
- **"No schedules found"**: Make sure the database isn't already populated with
  old/different test data, or check spelling matches a seeded route above.
- **Booking fails with seat error**: The number of comma-separated seat numbers
  you enter must exactly match the "Number of Seats" field.
- **Search returns nothing for a typo-free city**: Restart the app once with an
  empty database so `DataSeeder` can populate sample routes.
