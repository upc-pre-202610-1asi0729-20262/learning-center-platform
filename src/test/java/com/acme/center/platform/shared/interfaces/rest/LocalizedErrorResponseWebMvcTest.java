package com.acme.center.platform.shared.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LocalizedErrorResponseWebMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
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

    @RestController
    static class ThrowingTestController {
        @GetMapping("/test/error")
        public String error() {
            throw new RuntimeException("boom");
        }
    }
}


