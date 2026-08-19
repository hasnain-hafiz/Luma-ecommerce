package com.luma.commerce.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  java.util.Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
