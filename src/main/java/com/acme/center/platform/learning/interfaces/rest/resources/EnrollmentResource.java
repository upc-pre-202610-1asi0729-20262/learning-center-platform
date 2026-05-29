package com.acme.center.platform.learning.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enrollment resource.
 */
@Schema(
    name = "EnrollmentResponse",
    description = "Enrollment information response",
    example = "{\"enrollmentId\": 1, \"studentRecordId\": \"STU-2025-001\", \"courseId\": 1, \"status\": \"PENDING\"}"
)
public record EnrollmentResource(
    @Schema(description = "Enrollment unique identifier", example = "1")
    Long enrollmentId,

    @Schema(description = "Student record identifier", example = "STU-2025-001")
    String studentRecordId,

    @Schema(description = "Course identifier", example = "1")
    Long courseId,

    @Schema(description = "Enrollment status", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "REJECTED", "CANCELLED"})
    String status
) {
}
