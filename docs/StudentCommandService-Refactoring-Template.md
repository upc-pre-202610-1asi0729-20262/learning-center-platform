# StudentCommandService Refactoring Template

This document shows the complete step-by-step refactoring of `StudentCommandService` from exception-based to Result pattern.

## Current State (Before)

### StudentCommandService Interface
```java
package com.acme.center.platform.learning.application.commandservices;

public interface StudentCommandService {
    AcmeStudentRecordId handle(CreateStudentCommand command);
    AcmeStudentRecordId handle(CreateStudentByProfileIdCommand command);
    AcmeStudentRecordId handle(UpdateStudentMetricsOnTutorialCompletedCommand command);
}
```

### StudentCommandServiceImpl (Current)
```java
package com.acme.center.platform.learning.application.internal.commandservices;

@Service
public class StudentCommandServiceImpl implements StudentCommandService {
    private final StudentRepository studentRepository;
    private final ExternalProfileService externalProfileService;

    @Override
    public AcmeStudentRecordId handle(CreateStudentCommand command) {
        // May throw StudentNotFoundException or other exceptions
        var student = new Student(command);
        return studentRepository.save(student).getStudentRecordId();
    }

    @Override
    public AcmeStudentRecordId handle(CreateStudentByProfileIdCommand command) {
        // May throw StudentNotFoundException
        var student = new Student(new CreateStudentCommand(...));
        return studentRepository.save(student).getStudentRecordId();
    }

    @Override
    public AcmeStudentRecordId handle(UpdateStudentMetricsOnTutorialCompletedCommand command) {
        // May throw StudentNotFoundException
        var student = studentRepository.findById(command.studentRecordId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        student.updateMetricsOnTutorialCompleted(command.tutorialId());
        return studentRepository.save(student).getStudentRecordId();
    }
}
```

### StudentsController (Current)
```java
@RestController
@RequestMapping("/api/v1/students")
public class StudentsController {
    @PostMapping
    public ResponseEntity<StudentResource> createStudent(@RequestBody CreateStudentResource resource) {
        var createStudentCommand = CreateStudentCommandFromResourceAssembler.toCommandFromResource(resource);
        var acmeStudentRecordId = studentCommandService.handle(createStudentCommand);
        
        if (acmeStudentRecordId.studentRecordId().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var student = studentQueryService.handle(
                new GetStudentByAcmeStudentRecordIdQuery(acmeStudentRecordId));
        
        if (student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return new ResponseEntity<>(
                StudentResourceFromEntityAssembler.toResourceFromEntity(student.get()),
                HttpStatus.CREATED);
    }
}
```

---

## Refactored State (After)

### Step 1: Update StudentCommandService Interface

```java
package com.acme.center.platform.learning.application.commandservices;

import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;

public interface StudentCommandService {
    /**
     * Creates a new student from profile data.
     * 
     * @param command the CreateStudentCommand containing student data
     * @return Result containing the new student's AcmeStudentRecordId on success,
     *         or ApplicationError on failure (validation, not found, etc.)
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand command);

    /**
     * Creates a new student triggered by a profile creation event.
     * Reactive operation that fetches profile data and creates student.
     * 
     * @param command the CreateStudentByProfileIdCommand containing profile id
     * @return Result containing the new student's AcmeStudentRecordId on success,
     *         or ApplicationError on failure (profile not found, etc.)
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentByProfileIdCommand command);

    /**
     * Updates student metrics after completing a tutorial.
     * 
     * @param command the UpdateStudentMetricsOnTutorialCompletedCommand
     * @return Result containing the updated student's AcmeStudentRecordId on success,
     *         or ApplicationError on failure (student not found, etc.)
     */
    Result<AcmeStudentRecordId, ApplicationError> handle(UpdateStudentMetricsOnTutorialCompletedCommand command);
}
```

### Step 2: Refactor StudentCommandServiceImpl

```java
package com.acme.center.platform.learning.application.internal.commandservices;

import com.acme.center.platform.learning.application.commandservices.StudentCommandService;
import com.acme.center.platform.learning.domain.exceptions.StudentNotFoundException;
import com.acme.center.platform.learning.domain.model.aggregates.Student;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentCommand;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentByProfileIdCommand;
import com.acme.center.platform.learning.domain.model.commands.UpdateStudentMetricsOnTutorialCompletedCommand;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.infrastructure.persistence.jpa.repositories.StudentRepository;
import com.acme.center.platform.learning.application.internal.outboundservices.acl.ExternalProfileService;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudentCommandServiceImpl implements StudentCommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentCommandServiceImpl.class);

    private final StudentRepository studentRepository;
    private final ExternalProfileService externalProfileService;

    public StudentCommandServiceImpl(
            StudentRepository studentRepository,
            ExternalProfileService externalProfileService) {
        this.studentRepository = studentRepository;
        this.externalProfileService = externalProfileService;
    }

    @Override
    public Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand command) {
        // Validate command input
        if (command.firstName() == null || command.firstName().trim().isEmpty()) {
            return Result.failure(ApplicationError.validationError(
                    "firstName",
                    "First name is required"));
        }

        if (command.lastName() == null || command.lastName().trim().isEmpty()) {
            return Result.failure(ApplicationError.validationError(
                    "lastName",
                    "Last name is required"));
        }

        if (command.email() == null || command.email().trim().isEmpty()) {
            return Result.failure(ApplicationError.validationError(
                    "email",
                    "Email is required"));
        }

        // Check if student already exists by email
        var existingStudent = studentRepository.findByEmail(command.email());
        if (existingStudent.isPresent()) {
            return Result.failure(ApplicationError.conflict(
                    "Student",
                    "A student with email '%s' already exists".formatted(command.email())));
        }

        try {
            var student = new Student(command);
            var savedStudent = studentRepository.save(student);
            return Result.success(savedStudent.getStudentRecordId());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating student", e);
            return Result.failure(ApplicationError.unexpected(
                    "Student creation",
                    e.getMessage()));
        }
    }

    @Override
    public Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentByProfileIdCommand command) {
        try {
            // Fetch profile data from external service (other bounded context)
            var profileId = command.profileId();
            var profile = externalProfileService.fetchProfileDataByProfileId(profileId);

            if (profile == null) {
                return Result.failure(ApplicationError.notFound(
                        "Profile",
                        profileId.toString()));
            }

            // Check if student already exists for this profile
            var existingStudent = studentRepository.findByProfileId(profileId);
            if (existingStudent.isPresent()) {
                // Idempotent: return existing student
                return Result.success(existingStudent.get().getStudentRecordId());
            }

            // Create student from profile data
            var createCommand = new CreateStudentCommand(
                    profile.getFirstName(),
                    profile.getLastName(),
                    profile.getEmail());

            var student = new Student(createCommand);
            student.setProfileId(profileId);
            var savedStudent = studentRepository.save(student);

            return Result.success(savedStudent.getStudentRecordId());
        } catch (StudentNotFoundException e) {
            return Result.failure(ApplicationError.notFound(
                    "Profile",
                    command.profileId().toString()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating student from profile", e);
            return Result.failure(ApplicationError.unexpected(
                    "Student creation from profile",
                    e.getMessage()));
        }
    }

    @Override
    public Result<AcmeStudentRecordId, ApplicationError> handle(UpdateStudentMetricsOnTutorialCompletedCommand command) {
        try {
            var studentRecordId = command.studentRecordId();
            var student = studentRepository.findById(studentRecordId)
                    .orElse(null);

            if (student == null) {
                return Result.failure(ApplicationError.notFound(
                        "Student",
                        studentRecordId.studentRecordId()));
            }

            // Validate tutorial exists (optional, depends on business rules)
            // If needed: validate that tutorial exists before updating

            // Update metrics
            student.updateMetricsOnTutorialCompleted(command.tutorialId());
            var updatedStudent = studentRepository.save(student);

            return Result.success(updatedStudent.getStudentRecordId());
        } catch (Exception e) {
            LOGGER.error("Unexpected error while updating student metrics", e);
            return Result.failure(ApplicationError.unexpected(
                    "Student metrics update",
                    e.getMessage()));
        }
    }
}
```

### Step 3: Refactor StudentsController

```java
package com.acme.center.platform.learning.interfaces.rest;

import com.acme.center.platform.learning.domain.model.queries.GetStudentByAcmeStudentRecordIdQuery;
import com.acme.center.platform.learning.domain.model.valueobjects.AcmeStudentRecordId;
import com.acme.center.platform.learning.application.commandservices.StudentCommandService;
import com.acme.center.platform.learning.application.queryservices.StudentQueryService;
import com.acme.center.platform.learning.interfaces.rest.resources.CreateStudentResource;
import com.acme.center.platform.learning.interfaces.rest.resources.StudentResource;
import com.acme.center.platform.learning.interfaces.rest.transform.CreateStudentCommandFromResourceAssembler;
import com.acme.center.platform.learning.interfaces.rest.transform.StudentResourceFromEntityAssembler;
import com.acme.center.platform.shared.application.result.ApplicationError;
import com.acme.center.platform.shared.application.result.Result;
import com.acme.center.platform.shared.interfaces.rest.util.HttpErrorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/students", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Students", description = "Available Student Endpoints")
public class StudentsController {
    private final StudentCommandService studentCommandService;
    private final StudentQueryService studentQueryService;

    public StudentsController(
            StudentCommandService studentCommandService,
            StudentQueryService studentQueryService) {
        this.studentCommandService = studentCommandService;
        this.studentQueryService = studentQueryService;
    }

    /**
     * Create a new student
     */
    @PostMapping
    @Operation(summary = "Create a new student")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Student created"),
            @ApiResponse(responseCode = "400", description = "Bad request - validation failed"),
            @ApiResponse(responseCode = "409", description = "Conflict - student already exists")})
    public ResponseEntity<?> createStudent(@RequestBody CreateStudentResource resource) {
        var createStudentCommand = CreateStudentCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = studentCommandService.handle(createStudentCommand);

        // Handle success case
        if (result instanceof Result.Success<?, ?> success) {
            var studentRecordId = (AcmeStudentRecordId) success.value();
            var query = new GetStudentByAcmeStudentRecordIdQuery(studentRecordId);
            var student = studentQueryService.handle(query);

            if (student.isEmpty()) {
                // Should not happen, but handle gracefully
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApplicationError("INTERNAL_ERROR",
                                "Student created but not found on retrieval"));
            }

            var studentResource = StudentResourceFromEntityAssembler
                    .toResourceFromEntity(student.get());
            return new ResponseEntity<>(studentResource, HttpStatus.CREATED);
        }

        // Handle failure case
        if (result instanceof Result.Failure<?, ?> failure) {
            var error = (ApplicationError) failure.error();
            return HttpErrorMapper.toErrorResponse(error);
        }

        // Should not reach here, but provide fallback
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Get student by Acme Student Record ID
     */
    @GetMapping("/{studentRecordId}")
    @Operation(summary = "Get student by Acme Student Record ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")})
    public ResponseEntity<StudentResource> getStudentByAcmeStudentRecordId(
            @PathVariable String studentRecordId) {
        var acmeStudentRecordId = new AcmeStudentRecordId(studentRecordId);
        var query = new GetStudentByAcmeStudentRecordIdQuery(acmeStudentRecordId);
        var student = studentQueryService.handle(query);

        if (student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var studentResource = StudentResourceFromEntityAssembler
                .toResourceFromEntity(student.get());
        return ResponseEntity.ok(studentResource);
    }
}
```

### Step 4: Update ProfileCreatedEventHandler (in learning context)

```java
package com.acme.center.platform.learning.application.internal.eventhandlers;

import com.acme.center.platform.learning.application.commandservices.StudentCommandService;
import com.acme.center.platform.learning.domain.model.commands.CreateStudentByProfileIdCommand;
import com.acme.center.platform.profiles.interfaces.events.ProfileCreatedIntegrationEvent;
import com.acme.center.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("learningProfileCreatedEventHandler")
public class ProfileCreatedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCreatedEventHandler.class);

    private final StudentCommandService studentCommandService;

    public ProfileCreatedEventHandler(StudentCommandService studentCommandService) {
        this.studentCommandService = studentCommandService;
    }

    @EventListener
    public void on(ProfileCreatedIntegrationEvent event) {
        var command = new CreateStudentByProfileIdCommand(event.profileId());
        var result = studentCommandService.handle(command);

        // Handle result
        if (result.isSuccess()) {
            LOGGER.info("Successfully created student for profile ID: {}", event.profileId());
        } else {
            if (result instanceof Result.Failure<?, ?> failure) {
                var error = (com.acme.center.platform.shared.application.result.ApplicationError) failure.error();
                LOGGER.error("Failed to create student for profile {}: {} - {}",
                        event.profileId(),
                        error.code(),
                        error.message());
            }
        }
    }
}
```

---

## Summary of Changes

| Component | Change | Benefit |
|-----------|--------|---------|
| **Interface** | Return type changed from `AcmeStudentRecordId` to `Result<AcmeStudentRecordId, ApplicationError>` | Explicit error cases, type-safe |
| **Implementation** | No exceptions thrown; all errors wrapped in Result | All error paths captured, composable |
| **Controller** | Manual error handling with pattern matching | Properly maps errors to HTTP status codes |
| **Event Handler** | Checks Result status instead of catching exceptions | Cleaner, more composable error handling |

## Testing Examples

```java
@Test
void shouldReturnValidationError_whenFirstNameIsEmpty() {
    // Given
    var command = new CreateStudentCommand("", "Doe", "john@example.com");

    // When
    var result = studentCommandService.handle(command);

    // Then
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("VALIDATION_ERROR", error.code());
        assertTrue(error.message().contains("First name"));
    }
}

@Test
void shouldReturnSuccess_whenValidStudentCreated() {
    // Given
    var command = new CreateStudentCommand("John", "Doe", "john@example.com");

    // When
    var result = studentCommandService.handle(command);

    // Then
    assertTrue(result.isSuccess());
    if (result instanceof Result.Success<?, ?> success) {
        var studentId = (AcmeStudentRecordId) success.value();
        assertNotNull(studentId);
    }
}

@Test
void shouldReturnConflictError_whenStudentAlreadyExists() {
    // Given
    var email = "existing@example.com";
    studentRepository.save(createStudent(email));
    var command = new CreateStudentCommand("Jane", "Doe", email);

    // When
    var result = studentCommandService.handle(command);

    // Then
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("STUDENT_CONFLICT", error.code());
    }
}

@Test
void shouldReturnNotFound_whenProfileDoesNotExist() {
    // Given
    var command = new CreateStudentByProfileIdCommand(999L);

    // When
    var result = studentCommandService.handle(command);

    // Then
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("PROFILE_NOT_FOUND", error.code());
    }
}
```

---

## Rollout Checklist

- [ ] Review this document and example with team
- [ ] Update StudentCommandService interface
- [ ] Update StudentCommandServiceImpl implementation
- [ ] Update StudentsController
- [ ] Update ProfileCreatedEventHandler in learning context
- [ ] Add/update unit tests
- [ ] Run full test suite
- [ ] Test manually via API
- [ ] Repeat for CourseCommandService and EnrollmentCommandService
- [ ] Finally migrate IAM context (UserCommandService, RoleCommandService)

