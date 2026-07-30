package com.rdv.service.repository;

import com.rdv.service.entity.ServiceRDV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRDVRepository extends JpaRepository<ServiceRDV, UUID> {
    Optional<ServiceRDV> findByName(String name);
}
