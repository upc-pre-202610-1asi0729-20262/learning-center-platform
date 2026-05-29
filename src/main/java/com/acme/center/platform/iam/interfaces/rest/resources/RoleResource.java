package com.acme.center.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "RoleResponse",
    description = "Role information response",
    example = "{\"id\": 1, \"name\": \"STUDENT\"}"
)
public record RoleResource(
    @Schema(description = "Role unique identifier", example = "1")
    Long id,

    @Schema(description = "Role name", example = "STUDENT", allowableValues = {"STUDENT", "INSTRUCTOR", "ADMIN"})
    String name
) {
}
