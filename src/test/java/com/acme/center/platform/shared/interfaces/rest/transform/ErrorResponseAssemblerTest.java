package com.acme.center.platform.shared.interfaces.rest.transform;

import com.acme.center.platform.shared.application.result.ApplicationError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseAssemblerTest {

    @AfterEach
    void clearLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void toErrorResponseFromApplicationErrorUsesDefaultBundleMessage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        var error = ApplicationError.notFound("Profile", "123");
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(error);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("PROFILE_NOT_FOUND", response.getBody().code());
        assertEquals("Resource not found", response.getBody().message());
    }

    @Test
    void toErrorResponseFromApplicationErrorUsesSpanishBundleMessage() {
        LocaleContextHolder.setLocale(new Locale("es"));

        var error = ApplicationError.conflict("User", "Username already exists");
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(error);

        assertEquals(409, response.getStatusCode().value());
        assertEquals("USER_CONFLICT", response.getBody().code());
        assertEquals("Conflicto de recurso", response.getBody().message());
    }
}

