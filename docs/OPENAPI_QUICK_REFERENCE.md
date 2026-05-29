# OpenAPI Quick Reference Guide

**ACME Learning Center Platform**  
**Updated:** May 29, 2026

---

## Quick Start

### 1. Start the Application
```bash
cd learning-center-platform
mvn clean spring-boot:run
```

### 2. Access Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### 3. Download OpenAPI Spec
- **JSON:** http://localhost:8080/v3/api-docs
- **YAML:** http://localhost:8080/v3/api-docs.yaml

---

## API Endpoints Overview

### Authentication & Authorization
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/authentication/sign-up` | Register new user |
| POST | `/api/v1/authentication/sign-in` | Login and get JWT token |

### Users Management
| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/users` | List all users (requires auth) |
| GET | `/api/v1/users/{userId}` | Get user details (requires auth) |
| GET | `/api/v1/roles` | List all available roles (requires auth) |

### Courses
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/courses` | Create new course |
| GET | `/api/v1/courses` | List all courses |
| GET | `/api/v1/courses/{courseId}` | Get course details |
| PUT | `/api/v1/courses/{courseId}` | Update course |
| DELETE | `/api/v1/courses/{courseId}` | Delete course |
| POST | `/api/v1/courses/{courseId}/learning-path-items/{tutorialId}` | Add tutorial to course |

### Students
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/students` | Create new student |
| GET | `/api/v1/students/{studentRecordId}` | Get student details |
| GET | `/api/v1/students/{studentRecordId}/enrollments` | Get student's enrollments |

### Enrollments
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/enrollments` | Request enrollment |
| GET | `/api/v1/enrollments` | List all enrollments |
| POST | `/api/v1/enrollments/{enrollmentId}/confirmations` | Approve enrollment |
| POST | `/api/v1/enrollments/{enrollmentId}/rejections` | Reject enrollment |
| POST | `/api/v1/enrollments/{enrollmentId}/cancellations` | Cancel enrollment |

### Profiles
| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/profiles` | Create user profile |
| GET | `/api/v1/profiles` | List all profiles |
| GET | `/api/v1/profiles/{profileId}` | Get profile details |

---

## Authentication

### Getting JWT Token
1. Call `/api/v1/authentication/sign-in` with credentials:
```json
{
  "username": "john.doe",
  "password": "SecurePass123!"
}
```

2. Response includes JWT token:
```json
{
  "id": 1,
  "username": "john.doe",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Using JWT Token
Add to request headers:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Common Request Examples

### Create Course
```bash
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Introduction to Java",
    "description": "Learn Java fundamentals and best practices"
  }'
```

### Get All Courses
```bash
curl http://localhost:8080/api/v1/courses
```

### Update Course
```bash
curl -X PUT http://localhost:8080/api/v1/courses/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Advanced Java",
    "description": "Learn advanced Java concepts"
  }'
```

### Create Student
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "street": "123 Main St",
    "number": "Apt 4",
    "city": "Springfield",
    "postalCode": "12345",
    "country": "USA"
  }'
```

### Request Enrollment
```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentRecordId": "STU-2025-001",
    "courseId": 1
  }'
```

### Confirm Enrollment
```bash
curl -X POST http://localhost:8080/api/v1/enrollments/1/confirmations \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Response Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Successfully retrieved or updated resource |
| 201 | Created | Successfully created new resource |
| 204 | No Content | Successfully deleted resource |
| 400 | Bad Request | Invalid input data |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | User lacks required permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Duplicate record or invalid state |

---

## Error Response Format

All error responses follow this format:
```json
{
  "message": "User not found",
  "code": "USER_NOT_FOUND",
  "timestamp": "2026-05-29T17:30:00Z",
  "path": "/api/v1/users/999"
}
```

---

## Role-Based Access

### Available Roles
- `ADMIN` - Full system access
- `INSTRUCTOR` - Can create/manage courses
- `STUDENT` - Can view courses and enroll

### Protected Endpoints by Role
- Create Course: `INSTRUCTOR` or `ADMIN`
- Update Course: `INSTRUCTOR` or `ADMIN`
- Delete Course: `INSTRUCTOR` or `ADMIN`
- Manage Users: `ADMIN`
- Manage Roles: `ADMIN`

---

## Environment URLs

| Environment | URL |
|-------------|-----|
| Local Development | `http://localhost:8080` |
| Staging | `https://staging-api.acme-learning.com` |
| Production | `https://api.acme-learning.com` |

---

## Useful Tools

### Swagger UI
- Interactive API documentation
- Test endpoints directly in browser
- Auto-generated from OpenAPI spec

### cURL
- Command-line HTTP client
- Great for quick API testing
- Easy to script

### Postman
- Full-featured API client
- Import OpenAPI spec directly
- Environment management
- Test automation

### REST Client Extensions
- VS Code: REST Client extension
- IntelliJ: Built-in REST Client
- Easy to organize requests in `.http` files

---

## Example: Full User Journey

### Step 1: Register User
```bash
curl -X POST http://localhost:8080/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePass123!",
    "roles": ["STUDENT"]
  }'
```

### Step 2: Sign In
```bash
curl -X POST http://localhost:8080/api/v1/authentication/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "SecurePass123!"
  }'
```
Save the returned JWT token.

### Step 3: Create Student Profile
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "street": "123 Main St",
    "number": "Apt 4",
    "city": "Springfield",
    "postalCode": "12345",
    "country": "USA"
  }'
```

### Step 4: View Available Courses
```bash
curl http://localhost:8080/api/v1/courses
```

### Step 5: Request Enrollment
```bash
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentRecordId": "STU-2025-001",
    "courseId": 1
  }'
```

### Step 6: Check Enrollment Status
```bash
curl -X GET http://localhost:8080/api/v1/students/STU-2025-001/enrollments \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Troubleshooting

### 401 Unauthorized
- Sign in to get JWT token
- Add token to `Authorization` header
- Check token hasn't expired (7 days)

### 404 Not Found
- Verify resource ID is correct
- Check if resource was successfully created
- Consult Swagger UI for correct endpoint

### 400 Bad Request
- Review Swagger UI for required fields
- Check JSON syntax
- Verify field formats and constraints

### 409 Conflict
- Check if resource already exists
- Verify enrollment status
- May need to cancel/reject existing record first

---

## API Documentation Files

| File | Purpose |
|------|---------|
| `OPENAPI_IMPROVEMENT_REPORT.md` | Detailed analysis and recommendations |
| `OPENAPI_IMPLEMENTATION_SUMMARY.md` | Complete implementation details |
| `OPENAPI_QUICK_REFERENCE.md` | This file - quick reference |

---

## Support

For issues, questions, or suggestions:
- Email: support@acme-learning.com
- Documentation: https://acme-learning-platform.wiki.github.io/docs
- Issue Tracker: [Project Repository]

---

## Version History

**v1.0.0** - May 29, 2026
- Initial OpenAPI specification
- Complete endpoint documentation
- Schema examples for all resources
- Security annotations
- Server configuration

---

**Happy Coding! 🚀**


