package com.rdv.controller;

import com.rdv.dto.AdminResetConfirm;
import com.rdv.dto.AdminResetRequest;
import com.rdv.dto.AdminResetResponse;
import com.rdv.dto.LoginRequest;
import com.rdv.dto.LoginResponse;
import com.rdv.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse(false, "Email and password required."));
        }

        boolean isValid = adminAuthService.authenticate(request.email(), request.password());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid credentials."));
        }

        return ResponseEntity.ok(new LoginResponse(true, "Login success."));
    }

    @PostMapping("/request-reset")
    public ResponseEntity<AdminResetResponse> requestReset(@RequestBody AdminResetRequest request) {
        if (request == null || request.email() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AdminResetResponse(false, "Email required.", null));
        }

        adminAuthService.requestReset(request.email());
        return ResponseEntity.ok(new AdminResetResponse(true, "Reset email sent.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AdminResetResponse> resetPassword(@RequestBody AdminResetConfirm request) {
        if (request == null || request.token() == null || request.newPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AdminResetResponse(false, "Token and new password required.", null));
        }

        adminAuthService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(new AdminResetResponse(true, "Password updated.", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}