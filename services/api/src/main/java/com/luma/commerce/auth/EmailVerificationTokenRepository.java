package com.luma.commerce.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, UUID> {
  Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);
}
