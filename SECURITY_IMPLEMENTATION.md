# Student Center Management - Security Implementation

This document explains the JWT and security implementation in the Student Center Management application.

## Overview

The application now includes a complete authentication and authorization system with:
- User registration and login
- JWT (JSON Web Token) authentication
- Refresh token functionality
- Role-based access control
- Password encryption with BCrypt

## Architecture

### 1. Security Components

#### User Model (`model/User.java`)
- Implements `UserDetails` interface from Spring Security
- Stores user credentials and roles
- Fields: email, password, role, account status flags

#### JWT Token Provider (`security/JwtTokenProvider.java`)
- Generates and validates JWT tokens
- Manages access and refresh tokens
- Token expiration configuration
- Claims extraction and validation

#### Authentication Filter (`security/JwtAuthenticationFilter.java`)
- Intercepts HTTP requests
- Extracts JWT token from Authorization header (Bearer token)
- Validates token and sets authentication in security context
- Runs once per request

#### Security Configuration (`security/SecurityConfiguration.java`)
- Configures Spring Security
- Sets up authentication providers
- Defines authorization rules
- Enables JWT filter in the filter chain

#### User Details Service (`security/CustomUserDetailsService.java`)
- Loads user details from database
- Used by Spring Security for authentication

### 2. Services

#### Auth Service (`service/AuthService.java`)
Interface for authentication operations:
- `register(RegisterRequest)` - Register new user
- `login(LoginRequest)` - Login user
- `refreshToken(RefreshTokenRequest)` - Refresh expired access token

#### Auth Service Implementation (`service/impl/AuthServiceImpl.java`)
- Handles user registration with validation
- Authenticates users and generates tokens
- Refreshes access tokens using refresh tokens
- Encodes passwords with BCrypt

### 3. DTOs

- **LoginRequest** - Email and password for login
- **RegisterRequest** - Email, password, confirm password for registration
- **AuthResponse** - Contains access token, refresh token, and expiration time
- **RefreshTokenRequest** - Contains refresh token

### 4. Controllers

#### Auth Controller (`controller/AuthController.java`)
REST endpoints:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh-token` - Refresh access token
- `GET /api/auth/health` - Health check (public)

## API Endpoints

### Public Endpoints (No Authentication Required)
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh-token
GET /api/auth/health
```

### Protected Endpoints (Authentication Required)
```
POST /api/student - Create student (requires valid JWT)
```

## Token Format

### Access Token
- Type: JWT
- Algorithm: HS256 (HMAC with SHA-256)
- Expiration: 24 hours (configurable)
- Contains: email, role, issued time, expiration

### Refresh Token
- Type: JWT
- Expiration: 7 days (configurable)
- Can be used to obtain new access token

## Usage Examples

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "message": "User registered successfully"
}
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 3. Use Access Token to Access Protected Endpoint
```bash
curl -X POST http://localhost:8080/api/student \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com"
  }'
```

### 4. Refresh Access Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<REFRESH_TOKEN>"
  }'
```

## Configuration

JWT settings are configured in `application.properties`:

```properties
# JWT Secret (change in production!)
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationInStudentCenterApplicationSecurityModule

# Access Token Expiration (in milliseconds) - Default: 24 hours
jwt.expiration=86400000

# Refresh Token Expiration (in milliseconds) - Default: 7 days
jwt.refresh.expiration=604800000
```

## Security Best Practices

1. **Change the Secret Key in Production**: The default secret key should be replaced with a strong, unique key.

2. **Use HTTPS**: Always use HTTPS in production to prevent token interception.

3. **Token Storage**: Store tokens securely on the client side (HttpOnly cookies recommended over localStorage).

4. **Token Rotation**: Implement token rotation for enhanced security.

5. **Rate Limiting**: Consider adding rate limiting to prevent brute-force attacks.

6. **CORS**: The AuthController has CORS enabled for all origins. Restrict this in production.

7. **Password Requirements**: Enforce strong password requirements for new registrations.

## Dependencies Added

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## Database Schema

Two main tables are created:

### users table
- id: BIGINT (PK)
- email: VARCHAR (unique)
- password: VARCHAR
- role: VARCHAR
- is_enabled: BOOLEAN
- is_account_non_expired: BOOLEAN
- is_account_non_locked: BOOLEAN
- is_credentials_non_expired: BOOLEAN

### students table
- id: BIGINT (PK)
- firstName: VARCHAR
- lastName: VARCHAR
- email: VARCHAR (unique)

## Error Handling

The application includes comprehensive error handling:
- Invalid credentials return 401 Unauthorized
- User already exists returns 400 Bad Request
- Invalid token returns 401 Unauthorized
- Missing required fields return 400 Bad Request

## Future Enhancements

1. Add token blacklisting for logout functionality
2. Implement two-factor authentication (2FA)
3. Add refresh token rotation
4. Implement rate limiting on login attempts
5. Add audit logging for security events
6. Support OAuth2/OpenID Connect
7. Add role-based endpoint authorization

## Testing

Use the provided test files to verify:
- User registration and validation
- Login with correct and incorrect credentials
- Token refresh functionality
- Protected endpoint access with valid/invalid tokens


