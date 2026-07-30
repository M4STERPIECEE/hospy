package com.rdv.appointment.service;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    List<AppointmentResponse> findAll();
    Page<AppointmentResponse> findAll(Pageable pageable);
    List<AppointmentResponse> findByMonth(int year, int month);
    AppointmentResponse getById(UUID id);
    AppointmentResponse create(AppointmentRequest request);
    AppointmentResponse updateStatus(UUID id, AppointmentStatus status);
}
