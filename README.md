# SimpleBlog

A Spring Boot blog application with JWT authentication and OAuth2 social login.

## Features

- JWT-based authentication with refresh tokens
- Google OAuth2 social login
- Blog article CRUD operations
- Thymeleaf frontend
- H2 in-memory database for development

## Tech Stack

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Kotlin
- Thymeleaf
- H2 Database
- JWT (jjwt)

## Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/simpleblog.git
```

2. Create `src/main/resources/application.yml` with your configuration:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            scope:
              - email
              - profile

jwt:
  issuer: your-email@example.com
  secret: your-secret-key-minimum-256-bits
```

3. Run the application
```bash
./gradlew bootRun
```

## API Endpoints

- `POST /login` - User login
- `GET /articles` - Get all articles
- `POST /api/articles` - Create article
- `PUT /api/articles/{id}` - Update article
- `DELETE /api/articles/{id}` - Delete article
- `POST /api/token` - Refresh JWT token

## Testing

```bash
./gradlew test
```

## License

MIT