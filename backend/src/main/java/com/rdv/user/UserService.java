package com.rdv.user;

import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> findAll();
    Page<UserResponse> findPatients(Pageable pageable);
    UserResponse getById(UUID id);
    User findEntityById(UUID id);
    UserResponse create(UserRequest request);
    UserResponse findOrCreateByEmail(UserRequest request);
}
