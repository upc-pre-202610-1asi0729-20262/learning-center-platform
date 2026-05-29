package com.acme.center.platform.shared.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

/**
 * Standard error response DTO returned in error case from REST endpoints.
 * Provides structured error information to API clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        String code,
        String message,
        @Nullable String details) {

    /**
     * Creates an ErrorResponseDto from code and message.
     */
    public ErrorResponseDto(String code, String message) {
        this(code, message, null);
    }
}

