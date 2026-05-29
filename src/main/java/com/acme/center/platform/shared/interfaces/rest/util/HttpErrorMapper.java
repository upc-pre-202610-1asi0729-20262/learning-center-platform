package com.acme.center.platform.shared.interfaces.rest.util;

import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.interfaces.rest.resources.ErrorResource;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility for mapping ApplicationError to HTTP responses.
 * Provides consistent HTTP status code selection based on error codes.
 */
@NullMarked
public final class HttpErrorMapper {

    private HttpErrorMapper() {
        // utility class
    }

    /**
     * Maps an ApplicationError to an appropriate HTTP ResponseEntity.
     * Automatically selects the correct HTTP status code based on the error code.
     *
     * @param error the ApplicationError to map
     * @return a ResponseEntity with the appropriate HTTP status and error resource
     */
    public static ResponseEntity<ErrorResource> toErrorResponse(ApplicationError error) {
        HttpStatus status = mapErrorCodeToStatus(error.code());
        ErrorResource resource = new ErrorResource(error.code(), error.message(), error.details());
        return new ResponseEntity<>(resource, status);
    }

    /**
     * Determines the appropriate HTTP status code for a given error code.
     *
     * @param errorCode the error code string (e.g., "PROFILE_NOT_FOUND", "VALIDATION_ERROR")
     * @return the corresponding HttpStatus
     */
    public static HttpStatus mapErrorCodeToStatus(String errorCode) {
        return switch (errorCode) {
            case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
            case String s when s.endsWith("_NOT_FOUND") -> HttpStatus.NOT_FOUND;
            case "BUSINESS_RULE_VIOLATION" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case String s when s.endsWith("_CONFLICT") -> HttpStatus.CONFLICT;
            case "UNEXPECTED_ERROR" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
