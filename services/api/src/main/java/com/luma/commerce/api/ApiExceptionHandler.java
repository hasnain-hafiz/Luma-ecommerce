package com.luma.commerce.api;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiContracts.ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    var fields = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ApiContracts.FieldError(error.getField(), error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage()))
        .toList();
    return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", request, fields);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiContracts.ApiError> unexpected(Exception ex, HttpServletRequest request) {
    return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "The request could not be completed", request, List.of());
  }

  private ResponseEntity<ApiContracts.ApiError> response(HttpStatus status, String code, String message, HttpServletRequest request, List<ApiContracts.FieldError> fields) {
    return ResponseEntity.status(status).body(new ApiContracts.ApiError(Instant.now(), status.value(), code, message, request.getRequestURI(), fields));
  }
}
