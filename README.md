# Taskwell
Task manager application. Provides a REST API backend with Java and Spring Boot (gradle). Includes a simple **React frontend** for managing tasks via a web app.

## Backend (Spring Boot)

### Features
- REST API with CRUD operations for tasks
- Data persistence with H2 (in-memory dev DB)
- Gradle build system
- Layered architecture (Controller, Service, Repository, Model, DTO)
- Unit & integration testing (JUnit 5, Mockito)

### Tech Stack
- **Java 17**
- **Spring Boot** (REST API)
- **Spring Security**
- - **Spring Data JPA + H2**
- **Gradle**
- **JUnit 5 + Mockito** (testing)
- **Layered Architecture** (DTO, model, repository, service, controller)

### Run Locally
```bash
cd backend
./gradlew bootRun
```

### Backend Testing

Backend unit and integration tests are written with JUnit5 and Mockito. To run all these tests:

```bash
cd backend
./gradlew test
```

## Frontend (React)

### Features

- Modern SPA with React and React Router
- Task management UI (CRUD, status, profile, etc.)
- Authentication routes and error handling

### Tech Stack

- **React**
- **React Routers**
- **Vite** (build tool)
- **JavaScript**
- **Fetch API** (backend communication)
- **CSS**
- **ESLint**
- **Jest + React Testing Library**

### Prerequisites

- Node.js
- npm

### Run Locally
```bash
cd frontend
npm install
npm run dev
```
The frontend will run on port 5173 by default. For API calls, make sure the backend is running on port 8080.

### Frontend Testing

Frontend unit and integration tests are written with Jest and React Testing Library. To run all these tests:

```bash
cd frontend
npx jest
```

### UX and Accessibility

- All form fields, buttons, and error messages contain adequate ARIA attributes and are focusable.
- Status messages and loading spinners are consistent across the app.

## CI

Both the frontend and backend are automatically tested upon every push or pull request to `main`. See `.github/workflows/ci.yml` for details.

## Authentication

Users must create an account and log in to access Taskwell's features. This is done via the frontend; Spring Security is used to manage sessions in the backend. To create an account, a username, email are required. The requirements for each are as follows:

1. Username must be between 3 and 50 characters long and only contain letters, numbers, and nonconsecutuve dots and underscores.
2. E-mails must follow the conventional "email@example.com" format.
3. Passwords must be between 8 and 100 characters and contain at least one uppercase letter, one lowercase letter, one number, and one special symbol.

When the app is running, errors are handled as follows:

- **401 Unauthorized:** Shown to users attempting to access Taskwell's login-only pages before logging in. They are prompted to log in.
- **403 Forbidden:** Shown to logged-in users attempting to access another user's tasks.
- **404 Not Found:** Shown to users accesing invalid URLs or tasks that do not exist.

## API Documentation

With the backend running, you can open http://localhost:8080/swagger-ui.html to view interactive API documentation written by me. The Swagger UI allows you to view and test all endpoints.

## Contributions and Licensing

This project is licensed under the MIT License. Contributions are welcome!
