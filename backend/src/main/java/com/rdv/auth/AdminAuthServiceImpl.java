package com.rdv.auth;

import com.rdv.auth.dto.*;
import com.rdv.auth.exception.InvalidCredentialsException;
import com.rdv.auth.security.JwtTokenProvider;
import com.rdv.common.exception.InvalidStateException;
import com.rdv.common.exception.ResourceNotFoundException;
import com.rdv.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminRepository adminRepository;
    private final AdminPasswordResetRepository resetRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.admin.reset-ttl-minutes:15}")
    private long resetTtlMinutes;

    @Value("${app.admin.reset-url:http://localhost:5100/reset-password}")
    private String resetUrl;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(admin.getEmail(), "ADMIN");
        return new LoginResponse(true, "Login success.", token);
    }

    @Override
    @Transactional
    public AdminResetResponse requestReset(AdminResetRequest request) {
        Admin admin = adminRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Admin introuvable avec email: " + request.email()));

        String token = UUID.randomUUID().toString().replace("-", "");
        AdminPasswordReset reset = AdminPasswordReset.builder()
                .admin(admin)
                .token(token)
                .expiresAt(ZonedDateTime.now().plusMinutes(resetTtlMinutes))
                .used(false)
                .build();

        resetRepository.save(reset);
        String resetLink = resetUrl + "?token=" + token;
        emailService.sendResetEmail(admin.getEmail(), resetLink);
        return new AdminResetResponse(true, "Reset email sent.", token);
    }

    @Override
    @Transactional
    public AdminResetResponse resetPassword(AdminResetConfirm request) {
        AdminPasswordReset reset = resetRepository.findByToken(request.token())
                .orElseThrow(() -> new InvalidCredentialsException("Jeton de réinitialisation invalide"));

        if (reset.isUsed()) {
            throw new InvalidStateException("Ce jeton a déjà été utilisé");
        }
        if (reset.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new InvalidStateException("Ce jeton a expiré");
        }

        Admin admin = reset.getAdmin();
        admin.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        reset.setUsed(true);

        adminRepository.save(admin);
        resetRepository.save(reset);
        return new AdminResetResponse(true, "Password updated.", null);
    }
}
