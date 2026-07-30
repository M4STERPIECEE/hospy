package com.rdv.appointment.mapper;

import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.Appointment;
import com.rdv.service.entity.ServiceRDV;
import com.rdv.service.mapper.ServiceRDVMapper;
import com.rdv.user.entity.User;
import com.rdv.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ServiceRDVMapper.class})
public interface AppointmentMapper {

    AppointmentResponse toResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "service", source = "service")
    @Mapping(target = "appointmentDate", source = "request.appointmentDate")
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : com.rdv.appointment.entity.AppointmentStatus.PENDING)")
    @Mapping(target = "notes", source = "request.notes")
    Appointment toEntity(AppointmentRequest request, User user, ServiceRDV service);
}
