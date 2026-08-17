package com.luma.commerce.api;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
  private final HealthEndpoint healthEndpoint;
  private final String version;

  public HealthController(HealthEndpoint healthEndpoint, @Value("${info.app.version:dev}") String version) {
    this.healthEndpoint = healthEndpoint;
    this.version = version;
  }

  @GetMapping
  public ApiContracts.HealthResponse health() {
    var status = healthEndpoint.health().getStatus().getCode();
    return new ApiContracts.HealthResponse("luma-commerce-api", status, version, Instant.now());
  }
}
