package com.acme.center.platform.shared.interfaces.rest;

import com.acme.center.platform.shared.interfaces.rest.resources.ErrorResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    @AfterEach
    void clearLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void handleRuntimeExceptionUsesLocalizedUnexpectedMessage() {
        LocaleContextHolder.setLocale(new Locale("es"));

        var handler = new GlobalExceptionHandler();
        var response = handler.handleRuntimeException(new RuntimeException("boom"));
        var error = (ErrorResource) response.getBody();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("UNEXPECTED_ERROR", error.code());
        assertEquals("Error inesperado", error.message());
        assertEquals("boom", error.details());
    }
}


