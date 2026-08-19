package com.luma.commerce.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  java.util.Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
  @Modifying
  @Query("update RefreshTokenEntity token set token.revokedAt = CURRENT_TIMESTAMP where token.userId = :userId and token.revokedAt is null")
  int revokeAllForUser(@Param("userId") java.util.UUID userId);
}
