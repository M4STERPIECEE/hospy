package com.rdv.user.mapper;

import com.rdv.user.dto.UserRequest;
import com.rdv.user.dto.UserResponse;
import com.rdv.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", expression = "java(request.password() != null ? request.password() : \"CHANGE_ME\")")
    @Mapping(target = "role", expression = "java(request.role() != null ? request.role() : com.rdv.user.entity.UserRole.USER)")
    User toEntity(UserRequest request);
}
