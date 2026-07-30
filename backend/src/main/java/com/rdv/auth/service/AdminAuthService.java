package com.rdv.auth.service;

import com.rdv.auth.dto.*;

public interface AdminAuthService {
    LoginResponse authenticate(LoginRequest request);
    AdminResetResponse requestReset(AdminResetRequest request);
    AdminResetResponse resetPassword(AdminResetConfirm request);
}
