# taskwell
Task manager application. Provides a REST API backend with Java and Spring Boot (gradle). Will later include a **React frontend** for managing tasks via a web app.

## Backend (Spring Boot)

### Features
- REST API with CRUD operations for tasks
- Data persistence with H2 (in-memory dev DB)
- Gradle build system
- Layered architecture (Controller, Service, Repository, Model, DTO)
- Unit & integration testing (JUnit 5, Mockito)

### Tech Stack
- **Java 17**
- **Spring Boot**
- **Gradle**
- **Spring Data JPA + H2**
- **JUnit 5**

### Run Locally
```bash
cd backend
./gradlew bootRun