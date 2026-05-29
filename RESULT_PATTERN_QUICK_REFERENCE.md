# Result Pattern - Quick Reference Card

**Last Updated:** May 29, 2026 | **Status:** ✅ Ready for Production

---

## 30-Second Overview

The **Result pattern** replaces exception-based error handling with type-safe **`Result<T, E>`** that makes error cases explicit.

```java
// Instead of:
Optional<Profile> handle(CreateProfileCommand cmd);  // No error info!

// We now have:
Result<Profile, ApplicationError> handle(CreateProfileCommand cmd);  // Explicit error cases
```

---

## Core Types (3 Files)

### 1. `Result<T, E>` (Application Layer)
```
src/main/java/.../shared/application/result/
```

```java
Result<Profile, ApplicationError> result = ...;

// Success case
if (result instanceof Result.Success<?, ?> success) {
    Profile profile = (Profile) success.value();
    // ...
}

// Failure case
if (result instanceof Result.Failure<?, ?> failure) {
    ApplicationError error = (ApplicationError) failure.error();
    // ...
}

// Functional composition
result.map(p -> p.getName())
      .flatMap(n -> validateName(n))
      .recover(error -> Result.success("Unknown"));

// Safe extraction
Optional<Profile> opt = result.toOptional();
Profile p = result.getOrElse(defaultProfile);
```

### 2. `ApplicationError` (Structured Errors)
```
src/main/java/.../shared/application/result/
```

```java
// Create errors
ApplicationError error1 = ApplicationError.validationError("email", "Invalid format");
ApplicationError error2 = ApplicationError.notFound("Profile", "id=123");
ApplicationError error3 = ApplicationError.conflict("Profile", "Email already exists");
ApplicationError error4 = ApplicationError.businessRuleViolation("enrollment", "Already enrolled");

// Use in Result
return Result.failure(error);
```

### 3. `ErrorResponseDto` (REST API)
```
src/main/java/.../shared/interfaces/rest/dto/
```

What clients receive in HTTP responses:
```json
{
  "code": "PROFILE_CONFLICT",
  "message": "Conflict with Profile",
  "details": "Email 'user@example.com' already exists"
}
```

---

## HTTP Status Mapping (Automatic)

Use `HttpErrorMapper.toErrorResponse(error)` in controllers:

| Error Code | HTTP Status |
|---|---|
| `VALIDATION_ERROR` | 400 Bad Request |
| `*_NOT_FOUND` | 404 Not Found |
| `BUSINESS_RULE_VIOLATION` | 422 Unprocessable Entity |
| `*_CONFLICT` | 409 Conflict |
| `UNEXPECTED_ERROR` | 500 Internal Server Error |

---

## Implementation Patterns

### Pattern 1: Command Service Interface
```java
public interface StudentCommandService {
    Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand cmd);
}
```

### Pattern 2: Command Service Implementation
```java
@Override
public Result<AcmeStudentRecordId, ApplicationError> handle(CreateStudentCommand cmd) {
    // Validate
    if (cmd.firstName() == null || cmd.firstName().isEmpty()) {
        return Result.failure(ApplicationError.validationError(
                "firstName", "First name is required"));
    }
    
    // Check business rules
    if (studentRepository.existsByEmail(cmd.email())) {
        return Result.failure(ApplicationError.conflict(
                "Student", "Email '%s' already exists".formatted(cmd.email())));
    }
    
    // Execute
    try {
        var student = new Student(cmd);
        var saved = studentRepository.save(student);
        return Result.success(saved.getStudentRecordId());
    } catch (Exception e) {
        return Result.failure(ApplicationError.unexpected(
                "Student creation", e.getMessage()));
    }
}
```

### Pattern 3: REST Controller
```java
@PostMapping
public ResponseEntity<?> createStudent(@RequestBody CreateStudentResource resource) {
    var cmd = CreateStudentCommandFromResourceAssembler.toCommand(resource);
    var result = studentCommandService.handle(cmd);
    
    // Success case
    if (result instanceof Result.Success<?, ?> success) {
        var studentId = (AcmeStudentRecordId) success.value();
        var student = studentQueryService.handle(new Query(studentId));
        return new ResponseEntity<>(toResource(student.get()), HttpStatus.CREATED);
    }
    
    // Failure case
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        return HttpErrorMapper.toErrorResponse(error);
    }
    
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}
```

### Pattern 4: ACL/Facade (Cross-Context)
```java
public Long createStudent(...) {
    var result = studentCommandService.handle(command);
    return result.toOptional()
            .map(id -> id.studentRecordId())
            .orElse("0");
}
```

### Pattern 5: Event Handler
```java
@Service
@EventListener
public void on(ProfileCreatedIntegrationEvent event) {
    var cmd = new CreateStudentByProfileIdCommand(event.profileId());
    var result = studentCommandService.handle(cmd);
    
    if (result.isSuccess()) {
        logger.info("Student created for profile {}", event.profileId());
    } else if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        logger.error("Failed: {} - {}", error.code(), error.message());
    }
}
```

---

## Unit Testing

```java
// Test success case
@Test
void shouldReturnSuccess_whenValidCommandProvided() {
    var cmd = new CreateStudentCommand("John", "Doe", "john@example.com");
    var result = studentCommandService.handle(cmd);
    
    assertTrue(result.isSuccess());
    if (result instanceof Result.Success<?, ?> success) {
        assertNotNull(success.value());
    }
}

// Test validation error
@Test
void shouldReturnValidationError_whenFirstNameIsEmpty() {
    var cmd = new CreateStudentCommand("", "Doe", "john@example.com");
    var result = studentCommandService.handle(cmd);
    
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("VALIDATION_ERROR", error.code());
    }
}

// Test conflict error
@Test
void shouldReturnConflictError_whenEmailAlreadyExists() {
    // Given
    studentRepository.save(createStudent("john@example.com"));
    var cmd = new CreateStudentCommand("Jane", "Doe", "john@example.com");
    
    // When
    var result = studentCommandService.handle(cmd);
    
    // Then
    assertTrue(result.isFailure());
    if (result instanceof Result.Failure<?, ?> failure) {
        var error = (ApplicationError) failure.error();
        assertEquals("STUDENT_CONFLICT", error.code());
    }
}
```

---

## Migration Checklist

For each command service you migrate:

- [ ] Update interface signature to `Result<T, ApplicationError>`
- [ ] Update implementation to return `Result.success()` or `Result.failure()`
- [ ] Remove all `throw new Exception()`
- [ ] Update controller to use pattern matching for Result
- [ ] Import `HttpErrorMapper` in controller
- [ ] Add unit tests for success case
- [ ] Add unit tests for each failure case
- [ ] Update ACL/Facade if applicable
- [ ] Manual test via Swagger/API
- [ ] Code review

---

## Documentation

| Document | Purpose | Length |
|----------|---------|--------|
| **Result-Pattern-Guide.md** | Complete reference with all patterns | 450+ lines |
| **StudentCommandService-Template.md** | Before/after refactoring example | 380+ lines |
| **Result-Pattern-Strategy.md** | Decision framework & comparison | 320+ lines |
| **RESULT_PATTERN_IMPLEMENTATION.md** | What was delivered & next steps | 400+ lines |

👉 **Start here:** Read `Result-Pattern-Guide.md` (20 min)
👉 **Then use:** `StudentCommandService-Refactoring-Template.md` as your guide

---

## Common Questions

**Q: When should I use Result?**
A: Always in command services and operations that can fail in multiple ways. Skip for simple getters/queries.

**Q: Can I mix Result and exceptions?**
A: Avoid in same layer. It's OK to catch infrastructure exceptions and wrap in Result.

**Q: What about async/reactive?**
A: This is synchronous-only. For Mono/Flux use Reactor's error operators.

**Q: How do I test Result code?**
A: Check `result.isSuccess()` or pattern match `instanceof`. No expected exceptions needed.

**Q: Do I need to update query services?**
A: Optional. Queries usually have one failure mode (not found), so `Optional<T>` is fine.

---

## Files to Know

```
src/main/java/com/acme/center/platform/
├── shared/
│   ├── application/result/
│   │   ├── Result.java          ← Read this first
│   │   └── ApplicationError.java ← Business errors
│   └── interfaces/rest/
│       ├── dto/ErrorResponseDto.java
│       └── util/HttpErrorMapper.java ← Use in controllers
├── profiles/
│   ├── application/commandservices/ProfileCommandService.java [EXAMPLE]
│   ├── application/internal/commandservices/ProfileCommandServiceImpl.java [EXAMPLE]
│   └── interfaces/rest/ProfilesController.java [EXAMPLE]
```

**Key:** `ProfileCommandService` is the reference implementation. Use it as a template!

---

## Next Steps

1. ✅ **Read this card** (5 min)
2. ✅ **Study ProfileCommandService implementation** (15 min)
3. 🔄 **Refactor StudentCommandService** (using template)
4. 🔄 **Refactor CourseCommandService**
5. 🔄 **Refactor EnrollmentCommandService**
6. ➕ **Optional: Migrate IAM context**

---

## Support

- **Stuck?** Check `Result-Pattern-Guide.md` section "Best Practices" or "FAQ"
- **Need example?** Look at `StudentCommandService-Refactoring-Template.md`
- **Decision questions?** Read `Result-Pattern-Implementation-Strategy.md`
- **Search for:** Look for similar patterns in ProfilesController

---

**Remember:** 
- ✅ Type safety is your friend (compiler catches bugs)
- ✅ Pattern matching reads naturally (if-else for Success/Failure)
- ✅ Functional composition avoids try-catch hell
- ✅ Tests are simpler without exceptions

**Proceed with confidence!**

