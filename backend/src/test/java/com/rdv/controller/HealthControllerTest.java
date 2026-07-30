package com.rdv.controller;

import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.common.HealthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @MockitoBean private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean private RestAccessDeniedHandler accessDeniedHandler;

    @Test
    void health_ShouldReturn200WithStatusUp() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andReturn();

        Map response = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(response).containsEntry("status", "UP");
    }
}
