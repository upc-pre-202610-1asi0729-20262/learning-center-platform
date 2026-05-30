package com.acme.center.platform.learning.domain.model.valueobjects;

/**
 * Value object representing the profile id.
 * @summary
 * This value object is used to represent the profile id.
 * It throws an IllegalArgumentException if the profile id is null or less than 1.
 * @param profileId The profile id. It cannot be null or less than 1.
 * @see IllegalArgumentException
 * @since 1.0
 */
public record ProfileId(Long profileId) {
    public ProfileId {
        if (profileId == null || profileId < 1) {
            throw new IllegalArgumentException("Profile id cannot be null or less than 1");
        }
    }

}
