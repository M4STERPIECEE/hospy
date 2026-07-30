package com.rdv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.service.controller.ServiceRDVController;
import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceStatus;
import com.rdv.service.exception.ServiceRDVNotFoundException;
import com.rdv.service.service.ServiceRDVService;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceRDVController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceRDVControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServiceRDVService serviceRDVService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private RestAccessDeniedHandler accessDeniedHandler;

    private final UUID serviceId = UUID.randomUUID();
    private final ZonedDateTime now = ZonedDateTime.now();
    private final ServiceRDVResponse serviceResponse = new ServiceRDVResponse(
            serviceId, "Consultation", "General checkup",
            30, new BigDecimal("50.00"), ServiceStatus.ACTIVE, now
    );

    @Test
    void getAllServices_ShouldReturn200() throws Exception {
        given(serviceRDVService.findAll()).willReturn(List.of(serviceResponse));

        MvcResult result = mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andReturn();

        List<ServiceRDVResponse> services = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ServiceRDVResponse.class));
        assertThat(services).hasSize(1);
        assertThat(services.get(0).name()).isEqualTo("Consultation");
        assertThat(services.get(0).price()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void getServiceById_ShouldReturn200() throws Exception {
        given(serviceRDVService.getById(serviceId)).willReturn(serviceResponse);

        MvcResult result = mockMvc.perform(get("/api/v1/services/{id}", serviceId))
                .andExpect(status().isOk())
                .andReturn();

        ServiceRDVResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), ServiceRDVResponse.class);
        assertThat(response.id()).isEqualTo(serviceId);
        assertThat(response.status()).isEqualTo(ServiceStatus.ACTIVE);
    }

    @Test
    void getServiceById_NotFound_ShouldReturn404() throws Exception {
        given(serviceRDVService.getById(serviceId)).willThrow(new ServiceRDVNotFoundException(serviceId));

        MvcResult result = mockMvc.perform(get("/api/v1/services/{id}", serviceId))
                .andExpect(status().isNotFound())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "RESOURCE_NOT_FOUND");
    }

    @Test
    void createService_ShouldReturn201() throws Exception {
        ServiceRDVRequest request = new ServiceRDVRequest(
                "New Service", "Description", 45, new BigDecimal("80.00"), ServiceStatus.ACTIVE);
        ServiceRDVResponse response = new ServiceRDVResponse(
                UUID.randomUUID(), "New Service", "Description",
                45, new BigDecimal("80.00"), ServiceStatus.ACTIVE, now);
        given(serviceRDVService.create(request)).willReturn(response);

        MvcResult result = mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ServiceRDVResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(), ServiceRDVResponse.class);
        assertThat(created.name()).isEqualTo("New Service");
    }

    @Test
    void createService_WithEmptyName_ShouldReturn400() throws Exception {
        ServiceRDVRequest request = new ServiceRDVRequest(
                "", "Description", 45, new BigDecimal("80.00"), ServiceStatus.ACTIVE);

        MvcResult result = mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void createService_WithNullDuration_ShouldReturn400() throws Exception {
        ServiceRDVRequest request = new ServiceRDVRequest(
                "Service", "Description", null, new BigDecimal("80.00"), ServiceStatus.ACTIVE);

        MvcResult result = mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void updateService_ShouldReturn200() throws Exception {
        ServiceRDVRequest request = new ServiceRDVRequest(
                "Updated", "Updated desc", 60, new BigDecimal("100.00"), ServiceStatus.INACTIVE);
        ServiceRDVResponse response = new ServiceRDVResponse(
                serviceId, "Updated", "Updated desc",
                60, new BigDecimal("100.00"), ServiceStatus.INACTIVE, now);
        given(serviceRDVService.updateFields(serviceId, 60, new BigDecimal("100.00"), ServiceStatus.INACTIVE))
                .willReturn(response);

        MvcResult result = mockMvc.perform(patch("/api/v1/services/{id}", serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        ServiceRDVResponse updated = objectMapper.readValue(
                result.getResponse().getContentAsString(), ServiceRDVResponse.class);
        assertThat(updated.status()).isEqualTo(ServiceStatus.INACTIVE);
    }
}
