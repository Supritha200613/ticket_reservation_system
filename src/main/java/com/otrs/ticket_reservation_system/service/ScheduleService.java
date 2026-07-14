package com.otrs.ticket_reservation_system.service;

import com.otrs.ticket_reservation_system.entity.Schedule;
import com.otrs.ticket_reservation_system.entity.enums.ScheduleStatus;
import com.otrs.ticket_reservation_system.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> getActiveSchedules() {
        return scheduleRepository.findByStatusOrderByDepartureTimeAsc(ScheduleStatus.ACTIVE);
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> searchSchedules(String source, String destination) {
        List<Schedule> results = scheduleRepository.findAvailableSchedules(
                source.trim(), destination.trim(), ScheduleStatus.ACTIVE);
        // Only show schedules that still have seats available
        return results.stream()
                .filter(s -> s.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
