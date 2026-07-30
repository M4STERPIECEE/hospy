package com.rdv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdv.auth.controller.AuthController;
import com.rdv.auth.dto.AdminResetConfirm;
import com.rdv.auth.dto.AdminResetRequest;
import com.rdv.auth.dto.AdminResetResponse;
import com.rdv.auth.dto.LoginRequest;
import com.rdv.auth.dto.LoginResponse;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.auth.service.AdminAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AdminAuthService adminAuthService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @MockitoBean private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean private RestAccessDeniedHandler accessDeniedHandler;

    @Test
    void login_ShouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest("admin@test.com", "password");
        given(adminAuthService.authenticate(request)).willReturn(
                new LoginResponse(true, "Login successful", "token-123"));

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        assertThat(response.success()).isTrue();
        assertThat(response.token()).isEqualTo("token-123");
    }

    @Test
    void login_WithInvalidEmail_ShouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest("invalid", "password");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void login_WithEmptyFields_ShouldReturn400() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void requestReset_ShouldReturn200() throws Exception {
        AdminResetRequest request = new AdminResetRequest("admin@test.com");
        given(adminAuthService.requestReset(request)).willReturn(
                new AdminResetResponse(true, "Reset email sent"));

        MvcResult result = mockMvc.perform(post("/api/v1/auth/request-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AdminResetResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AdminResetResponse.class);
        assertThat(response.success()).isTrue();
    }

    @Test
    void resetPassword_ShouldReturn200() throws Exception {
        AdminResetConfirm request = new AdminResetConfirm("token-123", "newPassword");
        given(adminAuthService.resetPassword(request)).willReturn(
                new AdminResetResponse(true, "Password reset successfully"));

        MvcResult result = mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        AdminResetResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AdminResetResponse.class);
        assertThat(response.success()).isTrue();
    }

    @Test
    void resetPassword_WithEmptyFields_ShouldReturn400() throws Exception {
        AdminResetConfirm request = new AdminResetConfirm("", "");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }

    @Test
    void logout_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk());
    }
}
