package com.acme.center.platform.shared.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LocalizedErrorResponseWebMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        var validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsSpanishLocalizedMessageFromAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/test/error")
                        .header("Accept-Language", "es")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("UNEXPECTED_ERROR"))
                .andExpect(jsonPath("$.message").value("Error inesperado"))
                .andExpect(jsonPath("$.details").value("boom"));
    }

    @Test
    void returnsEnglishLocalizedMessageFromAcceptLanguageHeader() throws Exception {
        mockMvc.perform(get("/test/error")
                        .header("Accept-Language", "en")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("UNEXPECTED_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.details").value("boom"));
    }

    @Test
    void returnsSpanishLocalizedValidationDetailsFromAcceptLanguageHeader() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .header("Accept-Language", "es")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validacion fallida"))
                .andExpect(jsonPath("$.details").value("Campo name: es obligatorio"));
    }

    @RestController
    static class ThrowingTestController {
        @GetMapping("/test/error")
        public String error() {
            throw new RuntimeException("boom");
        }

        @PostMapping("/test/validation")
        public String validate(@Valid @RequestBody ValidationResource resource) {
            return resource.name();
        }
    }

    record ValidationResource(@NotBlank(message = "{validation.not-blank}") String name) {
    }
}


