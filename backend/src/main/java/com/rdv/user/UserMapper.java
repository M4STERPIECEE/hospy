package com.rdv.user;

import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserRequest request) {
        if (request == null) return null;
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .password(request.password() != null ? request.password() : "CHANGE_ME")
                .role(request.role() != null ? request.role() : UserRole.USER)
                .build();
    }
}
