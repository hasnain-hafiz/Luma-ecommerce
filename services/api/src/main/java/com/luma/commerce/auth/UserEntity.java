package com.luma.commerce.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class UserEntity {
  @Id private UUID id;
  private String email;
  private String passwordHash;
  @Enumerated(EnumType.STRING) private AuthContracts.Role role;
  private boolean active;
  private Instant createdAt;
  private Instant updatedAt;
  protected UserEntity() {}
  public static UserEntity customer(String email, String passwordHash) { var user = new UserEntity(); user.id = UUID.randomUUID(); user.email = email; user.passwordHash = passwordHash; user.role = AuthContracts.Role.CUSTOMER; user.active = true; user.createdAt = Instant.now(); user.updatedAt = Instant.now(); return user; }
  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public AuthContracts.Role getRole() { return role; }
  public boolean isActive() { return active; }
}
