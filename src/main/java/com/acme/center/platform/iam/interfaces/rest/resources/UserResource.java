package com.acme.center.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
    name = "UserResponse",
    description = "User information response",
    example = "{\"id\": 1, \"username\": \"john.doe\", \"roles\": [\"STUDENT\", \"INSTRUCTOR\"]}"
)
public record UserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User username", example = "john.doe")
    String username,

    @Schema(description = "User assigned roles", example = "[\"STUDENT\"]")
    List<String> roles
) {
}
