package com.reproequinos.vitaequus_api.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ApiError> handleForbidden(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of("FORBIDDEN", "Acesso negado"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(ApiError.of("VALIDATION_ERROR", "Dados inválidos", fieldErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException invalidFormatException
                && invalidFormatException.getTargetType().isEnum()) {
            return invalidEnumResponse(
                    extractFieldName(invalidFormatException),
                    invalidFormatException.getTargetType()
            );
        }

        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_REQUEST", "Corpo da requisição inválido"));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ApiError> handleInvalidFormat(InvalidFormatException ex) {
        if (ex.getTargetType().isEnum()) {
            return invalidEnumResponse(extractFieldName(ex), ex.getTargetType());
        }

        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_REQUEST", "Formato de campo inválido"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();

        if (requiredType != null && requiredType.isEnum()) {
            return invalidEnumResponse(ex.getName(), requiredType);
        }

        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_REQUEST", "Parâmetro inválido: " + ex.getName()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("DATA_INTEGRITY_VIOLATION", "Registro viola uma restrição do banco de dados"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String code = status == HttpStatus.UNAUTHORIZED ? "UNAUTHORIZED" : status.name();

        return ResponseEntity.status(status)
                .body(ApiError.of(code, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ApiError.of("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("INTERNAL_ERROR", "Erro interno no servidor"));
    }

    private ResponseEntity<ApiError> invalidEnumResponse(String fieldName, Class<?> enumType) {
        String acceptedValues = Arrays.stream(enumType.getEnumConstants())
                .map(Object::toString)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        String message = "Valor inválido para " + fieldName + ". Valores aceitos: " + acceptedValues;

        return ResponseEntity.badRequest()
                .body(ApiError.of("INVALID_ENUM", message));
    }

    private String extractFieldName(InvalidFormatException ex) {
        List<JacksonException.Reference> path = ex.getPath();

        if (path == null || path.isEmpty()) {
            return "campo";
        }

        String fieldName = path.get(path.size() - 1).getPropertyName();
        return fieldName != null ? fieldName : "campo";
    }
}
