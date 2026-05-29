package com.acme.center.platform.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Resource for creating a profile.
 */
public record CreateProfileResource(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String street,
        String number,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country) {
}
