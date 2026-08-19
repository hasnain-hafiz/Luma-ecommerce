package com.luma.commerce.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  java.util.Optional<UserEntity> findByEmailIgnoreCase(String email);
}
