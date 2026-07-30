package com.rdv.service;

import com.rdv.service.dto.ServiceRDVRequest;
import com.rdv.service.dto.ServiceRDVResponse;
import com.rdv.service.entity.ServiceRDV;
import com.rdv.service.entity.ServiceStatus;
import com.rdv.service.exception.ServiceRDVNotFoundException;
import com.rdv.service.mapper.ServiceRDVMapper;
import com.rdv.service.repository.ServiceRDVRepository;
import com.rdv.service.service.ServiceRDVServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRDVServiceImplTest {

    @Mock
    private ServiceRDVRepository serviceRDVRepository;

    @Mock
    private ServiceRDVMapper serviceRDVMapper;

    @InjectMocks
    private ServiceRDVServiceImpl serviceRDVService;

    private ServiceRDV sampleService;
    private ServiceRDVResponse sampleResponse;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        serviceId = UUID.randomUUID();
        sampleService = ServiceRDV.builder()
                .id(serviceId)
                .name("Consultation Généraliste")
                .durationMinutes(30)
                .price(BigDecimal.valueOf(50))
                .status(ServiceStatus.ACTIVE)
                .build();

        sampleResponse = new ServiceRDVResponse(serviceId, "Consultation Généraliste", null, 30, BigDecimal.valueOf(50), ServiceStatus.ACTIVE, null);
    }

    @Test
    void getById_Success() {
        when(serviceRDVRepository.findById(serviceId)).thenReturn(Optional.of(sampleService));
        when(serviceRDVMapper.toResponse(sampleService)).thenReturn(sampleResponse);

        ServiceRDVResponse response = serviceRDVService.getById(serviceId);

        assertNotNull(response);
        assertEquals("Consultation Généraliste", response.name());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(serviceRDVRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ServiceRDVNotFoundException.class, () -> serviceRDVService.getById(serviceId));
    }
}
