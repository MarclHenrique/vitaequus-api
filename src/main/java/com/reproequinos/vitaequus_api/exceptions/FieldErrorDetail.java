package com.reproequinos.vitaequus_api.exceptions;

public record FieldErrorDetail(
        String field,
        String message
) {
}
