package com.rdv.service;

import com.rdv.dto.PublicBookingRequest;
import com.rdv.entity.Appointment;
import com.rdv.entity.ServiceRDV;
import com.rdv.entity.User;
import com.rdv.repository.AppointmentRepository;
import com.rdv.repository.ServiceRepository;
import com.rdv.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Appointment> findAll(@NonNull Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findByMonth(int year, int month) {
        ZonedDateTime start = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime end = start.plusMonths(1).minusNanos(1);
        return appointmentRepository.findByAppointmentDateBetween(start, end);
    }

    public Appointment findById(@NonNull UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Transactional
    public Appointment create(@NonNull Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Transactional
    @SuppressWarnings("null")
    public Appointment createPublicBooking(PublicBookingRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .firstName(request.getFirstName())
                            .lastName(request.getLastName())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .password("PUBLIC_BOOKING")
                            .role("USER")
                            .build();
                    return userRepository.save(newUser);
                });
        ServiceRDV service = serviceRepository.findByName(request.getService())
                .orElseGet(() -> {
                    return serviceRepository.save(ServiceRDV.builder()
                            .name(request.getService())
                            .durationMinutes(30)
                            .price(java.math.BigDecimal.valueOf(50))
                            .build());
                });
        LocalDateTime localDateTime = LocalDateTime.parse(request.getDate() + "T" + request.getTime());
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Appointment appointment = Appointment.builder()
                .user(user)
                .service(service)
                .appointmentDate(zonedDateTime)
                .status("En attente")
                .build();

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment updateStatus(@NonNull UUID id, @NonNull String status) {
        Appointment appointment = findById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }
}