package com.luma.commerce.api;

import java.time.Instant;
import java.util.List;

public final class ApiContracts {
  private ApiContracts() {}

  public record HealthResponse(String service, String status, String version, Instant checkedAt) {}
  public record ApiError(Instant timestamp, int status, String code, String message, String path, List<FieldError> errors) {}
  public record FieldError(String field, String message) {}
}
