package com.acme.center.platform.learning.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Student resource.
 */
@Schema(
    name = "StudentResponse",
    description = "Student information response",
    example = "{\"acmeStudentRecordId\": \"STU-2025-001\", \"profileId\": 1, \"totalCompletedCourses\": 3, \"totalCompletedTutorials\": 15}"
)
public record StudentResource(
    @Schema(description = "Student record identifier", example = "STU-2025-001")
    String acmeStudentRecordId,

    @Schema(description = "Associated profile identifier", example = "1")
    Long profileId,

    @Schema(description = "Total number of courses completed", example = "3")
    Integer totalCompletedCourses,

    @Schema(description = "Total number of tutorials completed", example = "15")
    Integer totalCompletedTutorials
) {
}
