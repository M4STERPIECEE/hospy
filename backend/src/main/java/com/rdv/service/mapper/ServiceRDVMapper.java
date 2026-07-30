package com.rdv.service.mapper;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceRDV;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceRDVMapper {

    ServiceRDVResponse toResponse(ServiceRDV service);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : com.rdv.service.entity.ServiceStatus.ACTIVE)")
    ServiceRDV toEntity(ServiceRDVRequest request);
}
