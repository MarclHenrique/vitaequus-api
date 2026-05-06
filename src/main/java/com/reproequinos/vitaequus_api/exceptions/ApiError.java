package com.reproequinos.vitaequus_api.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        String code,
        String message,
        LocalDateTime timestamp,
        List<FieldErrorDetail> fieldErrors
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, LocalDateTime.now(), List.of());
    }

    public static ApiError of(String code, String message, List<FieldErrorDetail> fieldErrors) {
        return new ApiError(code, message, LocalDateTime.now(), fieldErrors);
    }
}
