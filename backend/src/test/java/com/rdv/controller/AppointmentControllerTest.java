package com.rdv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdv.appointment.controller.AppointmentController;
import com.rdv.appointment.dto.AppointmentRequest;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.appointment.exception.AppointmentNotFoundException;
import com.rdv.appointment.service.AppointmentService;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceStatus;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private RestAccessDeniedHandler accessDeniedHandler;

    private final UUID appointmentId = UUID.randomUUID();
    private final ZonedDateTime now = ZonedDateTime.now();
    private final ZonedDateTime appointmentDate = now.plusDays(1);

    private final UserResponse userResponse = new UserResponse(
            UUID.randomUUID(), "John", "Doe", "john@test.com",
            "0123456789", UserRole.USER, now
    );

    private final ServiceRDVResponse serviceResponse = new ServiceRDVResponse(
            UUID.randomUUID(), "Consultation", "General checkup",
            30, new BigDecimal("50.00"), ServiceStatus.ACTIVE, now
    );

    private final AppointmentResponse appointmentResponse = new AppointmentResponse(
            appointmentId, userResponse, serviceResponse,
            appointmentDate, AppointmentStatus.PENDING, "Some notes", now
    );

    @Test
    void getAllAppointments_ShouldReturn200() throws Exception {
        given(appointmentService.findAll()).willReturn(List.of(appointmentResponse));

        MvcResult result = mockMvc.perform(get("/api/v1/appointments"))
                .andExpect(status().isOk())
                .andReturn();

        List<AppointmentResponse> appointments = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AppointmentResponse.class));
        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).status()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void getAppointmentById_ShouldReturn200() throws Exception {
        given(appointmentService.getById(appointmentId)).willReturn(appointmentResponse);

        MvcResult result = mockMvc.perform(get("/api/v1/appointments/{id}", appointmentId))
                .andExpect(status().isOk())
                .andReturn();

        AppointmentResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AppointmentResponse.class);
        assertThat(response.status()).isEqualTo(AppointmentStatus.PENDING);
        assertThat(response.user().email()).isEqualTo("john@test.com");
    }

    @Test
    void getAppointmentById_NotFound_ShouldReturn404() throws Exception {
        given(appointmentService.getById(appointmentId))
                .willThrow(new AppointmentNotFoundException(appointmentId));

        MvcResult result = mockMvc.perform(get("/api/v1/appointments/{id}", appointmentId))
                .andExpect(status().isNotFound())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "RESOURCE_NOT_FOUND");
    }

    @Test
    void getCalendar_ShouldReturn200() throws Exception {
        given(appointmentService.findByMonth(2026, 7)).willReturn(List.of(appointmentResponse));

        MvcResult result = mockMvc.perform(get("/api/v1/appointments/calendar")
                        .param("year", "2026")
                        .param("month", "7"))
                .andExpect(status().isOk())
                .andReturn();

        List<AppointmentResponse> appointments = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, AppointmentResponse.class));
        assertThat(appointments).hasSize(1);
    }

    @Test
    void createAppointment_ShouldReturn201() throws Exception {
        AppointmentRequest request = new AppointmentRequest(
                userResponse.id(), serviceResponse.id(),
                appointmentDate, AppointmentStatus.PENDING, "Notes");
        given(appointmentService.create(any())).willReturn(appointmentResponse);

        MvcResult result = mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        AppointmentResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(), AppointmentResponse.class);
        assertThat(created.status()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void createAppointment_WithEmptyBody_ShouldReturn400() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void updateStatus_ShouldReturn200() throws Exception {
        AppointmentResponse updated = new AppointmentResponse(
                appointmentId, userResponse, serviceResponse,
                appointmentDate, AppointmentStatus.CONFIRMED, null, now);
        given(appointmentService.updateStatus(appointmentId, AppointmentStatus.CONFIRMED))
                .willReturn(updated);

        Map<String, String> body = Map.of("status", "CONFIRMED");
        MvcResult result = mockMvc.perform(patch("/api/v1/appointments/{id}/status", appointmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        AppointmentResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AppointmentResponse.class);
        assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);
    }
}
