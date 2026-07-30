package com.rdv.auth;

import com.rdv.auth.dto.LoginRequest;
import com.rdv.auth.dto.LoginResponse;
import com.rdv.auth.exception.InvalidCredentialsException;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AdminPasswordResetRepository resetRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AdminAuthServiceImpl adminAuthService;

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .id(UUID.randomUUID())
                .email("admin@example.com")
                .passwordHash("hashed_password")
                .build();
    }

    @Test
    void authenticate_InvalidEmail_ThrowsInvalidCredentialsException() {
        LoginRequest request = new LoginRequest("unknown@example.com", "password");
        when(adminRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> adminAuthService.authenticate(request));
    }

    @Test
    void authenticate_Success() {
        LoginRequest request = new LoginRequest("admin@example.com", "password");
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("password", "hashed_password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("admin@example.com", "ADMIN")).thenReturn("fake-jwt-token");

        LoginResponse response = adminAuthService.authenticate(request);

        assertTrue(response.success());
        assertEquals("fake-jwt-token", response.token());
    }
}
