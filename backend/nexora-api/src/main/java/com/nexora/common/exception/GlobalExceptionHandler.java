package com.nexora.common.exception;

import com.nexora.common.response.ApiResponse;
import com.nexora.common.response.ApiResponse.ValidationErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation failure occurred: {}", ex.getMessage());
        List<ValidationErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetail(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());

        ApiResponse<Void> response = ApiResponse.error("Validation failed", "VALIDATION_ERROR", details);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        log.error("API error occurred: [{}] {}", ex.getCode(), ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), ex.getCode());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error("An unexpected error occurred on the server", "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
