# Result Pattern Integration Guide

## Overview

The **Result pattern** has been integrated into the Learning Center Platform application layer. This replaces exception-based error handling with a type-safe, composable `Result<T, E>` type that makes error cases explicit.

## Why Result Pattern?

| Aspect | Exception-Based | Result Pattern |
|--------|-----------------|----------------|
| **Error Handling** | Implicit (must read docs) | Explicit in signature |
| **Control Flow** | Exceptions for control | Pure values + composition |
| **Type Safety** | Method can fail silently | Compiler enforces handling |
| **Testability** | Test exceptions with `@Test(expected=...)` | Test return values directly |
| **Composability** | Try-catch blocks | `flatMap`, `map`, `recover` |

## Core Types

### 1. `Result<T, E>` (Sealed Interface)

Located in: `shared/application/result/Result.java`

```java
// Success case
Result<Profile, ApplicationError> success = Result.success(profile);

// Failure case
Result<Profile, ApplicationError> failure = Result.failure(error);

// Pattern matching (Java 17+)
var response = switch (result) {
    case Success<Profile, ApplicationError>(var profile) -> handleSuccess(profile);
    case Failure<Profile, ApplicationError>(var error) -> handleFailure(error);
};

// Functional composition
result.map(profile -> profile.getName())
      .flatMap(name -> validateName(name))
      .recover(error -> Result.success(DEFAULT_NAME));
```

### 2. `ApplicationError` (Record)

Located in: `shared/application/result/ApplicationError.java`

Structured error representation with machine-readable `code` and human-readable `message`:

```java
// Factory methods for common error types
ApplicationError.validationError("email", "Email already in use");
ApplicationError.notFound("Profile", "profile-id-123");
ApplicationError.conflict("Profile", "Email 'user@example.com' already exists");
ApplicationError.businessRuleViolation("enrollment", "Student already enrolled in this course");
```

### 3. `ErrorResponseDto` (DTO)

Located in: `shared/interfaces/rest/dto/ErrorResponseDto.java`

What's sent to the client:

```json
{
  "code": "PROFILE_CONFLICT",
  "message": "Conflict with Profile",
  "details": "Email 'user@example.com' already exists"
}
```

### 4. `HttpErrorMapper` (Utility)

Located in: `shared/interfaces/rest/util/HttpErrorMapper.java`

Automatically maps error codes to HTTP status codes:

```java
"VALIDATION_ERROR"        → 400 Bad Request
"*_NOT_FOUND"             → 404 Not Found
"BUSINESS_RULE_VIOLATION" → 422 Unprocessable Entity
"*_CONFLICT"              → 409 Conflict
"UNEXPECTED_ERROR"        → 500 Internal Server Error
```

## Implementation Patterns

### Pattern 1: Refactoring a Service Interface

**Before:**
```java
public interface ProfileCommandService {
    Optional<Profile> handle(CreateProfileCommand command);
}
```

**After:**
```java
public interface ProfileCommandService {
    Result<Profile, ApplicationError> handle(CreateProfileCommand command);
}
```

### Pattern 2: Implementing a Command Service

**Before:**
```java
@Override
public Optional<Profile> handle(CreateProfileCommand command) {
    var email = new EmailAddress(command.email());
    if (profileRepository.existsByEmailAddress(email)) {
        throw new IllegalArgumentException("Email already exists");
    }
    var profile = new Profile(command);
    return Optional.of(profileRepository.save(profile));
}
```

**After:**
```java
@Override
public Result<Profile, ApplicationError> handle(CreateProfileCommand command) {
    var email = new EmailAddress(command.email());
    if (profileRepository.existsByEmailAddress(email)) {
        return Result.failure(ApplicationError.conflict(
                "Profile",
                "Email '%s' already exists".formatted(command.email())));
    }
    
    var profile = new Profile(command);
    try {
        var saved = profileRepository.save(profile);
        return Result.success(saved);
    } catch (Exception e) {
        return Result.failure(ApplicationError.unexpected(
                "Profile creation",
                e.getMessage()));
    }
}
```

### Pattern 3: REST Controller Response Handling

**Before:**
```java
@PostMapping
public ResponseEntity<ProfileResource> createProfile(@RequestBody CreateProfileResource resource) {
    var profile = profileCommandService.handle(command);
    if (profile.isEmpty()) return ResponseEntity.badRequest().build();
    return new ResponseEntity<>(toResource(profile.get()), HttpStatus.CREATED);
}
```

**After:**
```java
@PostMapping
public ResponseEntity<?> createProfile(@RequestBody CreateProfileResource resource) {
    var result = profileCommandService.handle(command);
    
    if (result instanceof Result.Success<?, ?> success) {
        var profile = (Profile) success.value();
        return new ResponseEntity<>(toResource(profile), HttpStatus.CREATED);
    }
    
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        return HttpErrorMapper.toErrorResponse(error);
    }
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}
```

### Pattern 4: ACL/Facade Layer (Cross-Context Communication)

```java
public Long createProfile(String firstName, String lastName, String email, ...) {
    var result = profileCommandService.handle(command);
    return result.toOptional()
            .map(profile -> profile.getId())
            .orElse(0L);  // Fallback for callers expecting null safety
}
```

### Pattern 5: Error Recovery

```java
var result = studentCommandService.handle(command)
        .recover(error -> {
            if (error.code().equals("STUDENT_NOT_FOUND")) {
                // Create the student first, then retry
                createStudent();
                return studentCommandService.handle(command);
            }
            return Result.failure(error);
        });
```

## Migration Strategy for Your Codebase

### Phase 1: Core Abstractions ✅ DONE
- ✅ `Result<T, E>` interface
- ✅ `ApplicationError` record
- ✅ `ErrorResponseDto` DTO
- ✅ `HttpErrorMapper` utility

### Phase 2: Profiles Context ✅ DONE
- ✅ `ProfileCommandService.handle()` → returns `Result<Profile, ApplicationError>`
- ✅ `ProfileCommandServiceImpl` updated to use Result
- ✅ `ProfilesController.createProfile()` updated to handle Result
- ✅ `ProfilesContextFacadeImpl` updated to use Result

### Phase 3: Learning Context (RECOMMENDED NEXT)
Services to migrate:
- `StudentCommandService` (handle with `CreateStudentCommand`, `CreateStudentByProfileIdCommand`, etc.)
- `CourseCommandService` (handle with `CreateCourseCommand`, `UpdateCourseCommand`, etc.)
- `EnrollmentCommandService` (handle with `RequestEnrollmentCommand`, `ConfirmEnrollmentCommand`, etc.)

Controllers to migrate:
- `StudentsController`
- `CoursesController` and `CourseLearningPathController`
- `EnrollmentsController` (if exists)

### Phase 4: IAM Context (RECOMMENDED FOLLOW-UP)
Services to migrate:
- `UserCommandService` (handle with `SignUpCommand`, etc.)
- `RoleCommandService` (handle with `SeedRolesCommand`, etc.)

Controllers to migrate:
- `UsersController`
- `RolesController`
- `AuthenticationController`

### Phase 5: Query Services (OPTIONAL)
Query services currently return `Optional<T>`. You can optionally migrate them to:
- `Result<T, ApplicationError>` for consistent error handling, OR
- Keep `Optional<T>` since queries are less likely to have multiple failure modes

## Code Examples

### Example 1: StudentCommandService Refactoring

**Current:**
```java
@Override
public AcmeStudentRecordId handle(CreateStudentCommand command) {
    // throws StudentNotFoundException or other exceptions
}
```

**Refactored:**
```java
@Override
public Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand command) {
    // validate input
    if (command.firstName() == null || command.firstName().isEmpty()) {
        return Result.failure(ApplicationError.validationError("firstName", "First name is required"));
    }
    
    try {
        var student = new Student(command);
        var saved = studentRepository.save(student);
        return Result.success(saved.getStudentRecordId());
    } catch (StudentNotFoundException e) {
        return Result.failure(ApplicationError.notFound("Student", command.profileId().toString()));
    } catch (Exception e) {
        return Result.failure(ApplicationError.unexpected("Student creation", e.getMessage()));
    }
}
```

### Example 2: Composing Multiple Operations

```java
// Chain operations that might fail at different points
var result = courseRepository.findById(courseId)
        .map(course -> {
            course.addTutorialToLearningPath(tutorialId);
            return courseRepository.save(course);
        })
        .orElse(Result.failure(ApplicationError.notFound("Course", courseId.toString())))
        .flatMap(course -> validateLearningPath(course))
        .recover(error -> {
            logger.warn("Failed to add tutorial: {}", error.message());
            return Result.failure(error);
        });
```

## Best Practices

### ✅ DO

- **Use Result for operations that can fail**: Commands, validations, operations with side effects
- **Provide meaningful error codes**: Use pattern like `RESOURCE_TYPE_ERROR_KIND` (e.g., `PROFILE_CONFLICT`, `STUDENT_NOT_FOUND`)
- **Include details for debugging**: The `details` field should have actionable information
- **Use sealed interface pattern matching** (Java 17+) instead of if-else chains when possible
- **Compose operations with `flatMap`**: Cleaner than try-catch blocks
- **Test with return values**: Much simpler than exception testing

### ❌ DON'T

- **Don't mix Result with exceptions**: Pick one per layer (Result in app layer, exceptions for infrastructure/framework)
- **Don't ignore errors**: Always handle both Success and Failure cases
- **Don't make Result types generic across layers**: Application layer uses `Result<T, ApplicationError>`, infrastructure uses exceptions
- **Don't use Result for flow control in synchronous code** (like loops): Only for operations that conceptually succeed/fail
- **Don't expose internal errors to clients**: Map infrastructure errors to domain-level ApplicationError codes

## Testing Result-Based Code

### Unit Test Example

```java
@Test
void shouldReturnConflictError_whenProfileEmailAlreadyExists() {
    // Given
    var command = new CreateProfileCommand("John", "Doe", "existing@example.com", ...);
    profileRepository.save(existingProfileWithEmail("existing@example.com"));
    
    // When
    var result = profileCommandService.handle(command);
    
    // Then
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("PROFILE_CONFLICT", error.code());
    }
}

@Test
void shouldReturnSuccess_whenProfileCreatedSuccessfully() {
    // Given
    var command = new CreateProfileCommand("Jane", "Doe", "new@example.com", ...);
    
    // When
    var result = profileCommandService.handle(command);
    
    // Then
    assertTrue(result.isSuccess());
    if (result instanceof Result.Success<?, ?> success) {
        var profile = (Profile) success.value();
        assertEquals("Jane", profile.getName().firstName());
    }
}
```

## Integration with Existing Code

### Working with ACL Facades

Facades between bounded contexts should:
1. Accept Result from service
2. Extract value with `toOptional()` or `getOrElse()`
3. Return simple values to external contexts

```java
public Long createProfile(...) {
    var result = profileCommandService.handle(command);
    return result.toOptional()
            .map(profile -> profile.getId())
            .orElse(0L);
}
```

### Working with Event Handlers

Event handlers already work seamlessly:

```java
@Service
@EventListener
public void on(ProfileCreatedIntegrationEvent event) {
    var command = new CreateStudentByProfileIdCommand(event.profileId());
    var result = studentCommandService.handle(command);
    
    if (result.isFailure()) {
        logger.error("Failed to create student for profile {}: {}", 
                event.profileId(), 
                result instanceof Result.Failure<?, ?> f ? f.error() : "unknown error");
    }
}
```

## FAQ

**Q: Should I Use Result for all methods?**
A: No. Use Result only for:
- Command handlers (operations that mutate state)
- Operations that have multiple failure modes worth distinguishing
- Application layer operations exposed to REST controllers

Skip Result for:
- Queries that naturally return Optional
- Framework/infrastructure code (use exceptions)
- Pure utility functions that shouldn't fail

**Q: Can I use Result with Spring AOP/Transactions?**
A: Yes. Result is just a return type. `@Transactional` works normally. Exception-based rollback still works because actual exceptions are caught in try-catch and wrapped in Result.failure().

**Q: How do I handle Result in reactive code?**
A: This implementation is synchronous. For Mono/Flux, use Vavr's Either or Project Reactor's error operators.

**Q: What about null safety with Result?**
A: Result itself is null-safe (@NullMarked). Always handle both Success and Failure branches explicitly.

## References

- **Java 17 Pattern Matching**: https://openjdk.java.net/jeps/406
- **Functional Error Handling in Java**: https://www.baeldung.com/java-error-handling
- **Railway-Oriented Programming**: https://fsharpforfunandprofit.com/posts/recipe-part2/
- **DDD Error Handling**: https://vaughnvernon.com/

