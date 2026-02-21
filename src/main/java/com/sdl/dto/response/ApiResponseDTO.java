package com.sdl.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponseDTO(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponseDTOBuilder<T> builder() {
        return new ApiResponseDTOBuilder<>();
    }

    public static class ApiResponseDTOBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private LocalDateTime timestamp;

        ApiResponseDTOBuilder() {
        }

        public ApiResponseDTOBuilder<T> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseDTOBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseDTOBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResponseDTOBuilder<T> timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponseDTO<T> build() {
            return new ApiResponseDTO<>(
                    success,
                    message,
                    data,
                    timestamp != null ? timestamp : LocalDateTime.now()
            );
        }
    }

    // Getters only (immutable response)

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Static factory methods

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> success(String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}