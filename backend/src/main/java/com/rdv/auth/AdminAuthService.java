package com.rdv.auth;

import com.rdv.auth.dto.AdminResetConfirm;
import com.rdv.auth.dto.AdminResetRequest;
import com.rdv.auth.dto.AdminResetResponse;
import com.rdv.auth.dto.LoginRequest;
import com.rdv.auth.dto.LoginResponse;

public interface AdminAuthService {
    LoginResponse authenticate(LoginRequest request);
    AdminResetResponse requestReset(AdminResetRequest request);
    AdminResetResponse resetPassword(AdminResetConfirm request);
}
