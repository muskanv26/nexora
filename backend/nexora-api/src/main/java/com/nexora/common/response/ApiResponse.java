package com.nexora.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ApiError(message, code, null))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String code, List<ValidationErrorDetail> details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(new ApiError(message, code, details))
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiError {
        private String message;
        private String code;
        private List<ValidationErrorDetail> details;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationErrorDetail {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
