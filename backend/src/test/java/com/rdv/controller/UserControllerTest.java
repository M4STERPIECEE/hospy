package com.rdv.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.auth.security.RestAccessDeniedHandler;
import com.rdv.auth.security.RestAuthenticationEntryPoint;
import com.rdv.user.controller.UserController;
import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.entity.UserRole;
import com.rdv.user.exception.UserNotFoundException;
import com.rdv.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserService userService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @MockitoBean private RestAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean private RestAccessDeniedHandler accessDeniedHandler;

    private final UUID userId = UUID.randomUUID();
    private final ZonedDateTime now = ZonedDateTime.now();
    private final UserResponse userResponse = new UserResponse(
            userId, "John", "Doe", "john@test.com", "0123456789",
            UserRole.USER, now
    );

    @Test
    void getAllUsers_ShouldReturn200() throws Exception {
        given(userService.findAll()).willReturn(List.of(userResponse));

        MvcResult result = mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andReturn();

        List<UserResponse> users = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserResponse.class));
        assertThat(users).hasSize(1);
        assertThat(users.get(0).email()).isEqualTo("john@test.com");
    }

    @Test
    void getUserById_ShouldReturn200() throws Exception {
        given(userService.getById(userId)).willReturn(userResponse);

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponse.class);
        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.firstName()).isEqualTo("John");
    }

    @Test
    void getUserById_NotFound_ShouldReturn404() throws Exception {
        given(userService.getById(userId)).willThrow(new UserNotFoundException(userId));

        MvcResult result = mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "RESOURCE_NOT_FOUND");
    }

    @Test
    void createUser_ShouldReturn201() throws Exception {
        UserRequest request = new UserRequest("Jane", "Smith", "jane@test.com",
                "0987654321", "password", UserRole.USER);
        UserResponse response = new UserResponse(UUID.randomUUID(), "Jane", "Smith",
                "jane@test.com", "0987654321", UserRole.USER, now);
        given(userService.create(request)).willReturn(response);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse created = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponse.class);
        assertThat(created.email()).isEqualTo("jane@test.com");
    }

    @Test
    void createUser_WithEmptyFirstName_ShouldReturn400() throws Exception {
        UserRequest request = new UserRequest("", "Smith", "jane@test.com",
                "0987654321", "password", UserRole.USER);

        MvcResult result = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        Map error = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);
        assertThat(error).containsEntry("code", "VALIDATION_ERROR");
    }
}
