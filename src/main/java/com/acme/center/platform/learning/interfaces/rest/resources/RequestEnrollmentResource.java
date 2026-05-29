package com.acme.center.platform.learning.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request enrollment resource.
 */
@Schema(
    name = "EnrollmentRequest",
    description = "Request payload for enrolling a student in a course",
    example = "{\"studentRecordId\": \"STU-2025-001\", \"courseId\": 1}"
)
public record RequestEnrollmentResource(
    @Schema(
        description = "Student record identifier",
        example = "STU-2025-001"
    )
    String studentRecordId,

    @Schema(
        description = "Course identifier to enroll in",
        example = "1"
    )
    Long courseId
) {
    /**
     * Validates the resource.
     * @throws IllegalArgumentException if the student record id or course id is null or blank.
     */
    public RequestEnrollmentResource {
        if (studentRecordId == null || studentRecordId.isBlank()) {
            throw new IllegalArgumentException("Student record id is required");
        }
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course id is required");
        }
    }
}
