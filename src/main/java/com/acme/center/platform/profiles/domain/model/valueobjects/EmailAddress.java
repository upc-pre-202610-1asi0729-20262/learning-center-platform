package com.acme.center.platform.profiles.domain.model.valueobjects;

import jakarta.validation.constraints.Email;

/**
 * EmailAddress Value Object.
 */
public record EmailAddress(@Email String address) {
    public EmailAddress {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Email address must not be null or blank");
        }
    }

    public String getAddress() {
        return address;
    }
}
