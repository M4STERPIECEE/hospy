package com.rdv.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminPasswordResetRepository extends JpaRepository<AdminPasswordReset, UUID> {
    Optional<AdminPasswordReset> findByToken(String token);
}
