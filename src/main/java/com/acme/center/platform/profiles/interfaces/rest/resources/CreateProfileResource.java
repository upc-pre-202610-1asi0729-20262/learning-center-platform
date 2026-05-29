package com.acme.center.platform.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for creating a profile.
 */
public record CreateProfileResource(
        @NotBlank(message = "{validation.not-blank}") String firstName,
        @NotBlank(message = "{validation.not-blank}") String lastName,
        @NotBlank(message = "{validation.not-blank}") @Email(message = "{validation.email}") String email,
        @NotBlank(message = "{validation.not-blank}") String street,
        String number,
        @NotBlank(message = "{validation.not-blank}") String city,
        @NotBlank(message = "{validation.not-blank}") String postalCode,
        @NotBlank(message = "{validation.not-blank}") String country) {
}
