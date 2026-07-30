package com.rdv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdv.appointment.dto.AppointmentResponse;
import com.rdv.appointment.entity.AppointmentStatus;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.booking.controller.PublicBookingController;
import com.rdv.booking.dto.PublicBookingRequest;
import com.rdv.booking.dto.PublicBookingResponse;
import com.rdv.booking.service.PublicBookingService;
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
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicBookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PublicBookingService publicBookingService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private RestAccessDeniedHandler accessDeniedHandler;

    private final ZonedDateTime now = ZonedDateTime.now();

    private final UserResponse userResponse = new UserResponse(
            UUID.randomUUID(), "John", "Doe", "john@test.com",
            "0123456789", UserRole.USER, now
    );

    private final ServiceRDVResponse serviceResponse = new ServiceRDVResponse(
            UUID.randomUUID(), "Consultation", "General checkup",
            30, new BigDecimal("50.00"), ServiceStatus.ACTIVE, now
    );

    private final AppointmentResponse appointmentResponse = new AppointmentResponse(
            UUID.randomUUID(), userResponse, serviceResponse,
            now.plusDays(1), AppointmentStatus.PENDING, null, now
    );

    private final PublicBookingResponse bookingResponse = new PublicBookingResponse(
            userResponse, serviceResponse, appointmentResponse
    );

    @Test
    void createPublicBooking_ShouldReturn201() throws Exception {
        PublicBookingRequest request = new PublicBookingRequest(
                "John", "Doe", "john@test.com", "0123456789",
                "Consultation", "2026-08-15", "10:00");
        given(publicBookingService.book(request)).willReturn(bookingResponse);

        MvcResult result = mockMvc.perform(post("/api/v1/public/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        PublicBookingResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), PublicBookingResponse.class);
        assertThat(response.user().email()).isEqualTo("john@test.com");
        assertThat(response.service().name()).isEqualTo("Consultation");
    }

    @Test
    void createPublicBooking_WithEmptyFields_ShouldReturn400() throws Exception {
        PublicBookingRequest request = new PublicBookingRequest(
                "", "", "", "", "", "", "");

        MvcResult result = mockMvc.perform(post("/api/v1/public/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void legacyPublicBooking_ShouldReturn200() throws Exception {
        PublicBookingRequest request = new PublicBookingRequest(
                "John", "Doe", "john@test.com", "0123456789",
                "Consultation", "2026-08-15", "10:00");
        given(publicBookingService.book(request)).willReturn(bookingResponse);

        MvcResult result = mockMvc.perform(post("/api/v1/appointments/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AppointmentResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AppointmentResponse.class);
        assertThat(response.status()).isEqualTo(AppointmentStatus.PENDING);
    }
}
