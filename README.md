# Library Management System

A full-stack web application for managing a library's collection, authors, and reservations. This project is built using modern Java technologies and follows a layered architecture to ensure maintainability and scalability.

## 🚀 Key Features

- **Book Management:** Create, read, update, and delete books in the library's catalog.
- **Author Profiles:** Manage author information and their associated works.
- **Reservation System:** Allow users to reserve books and track reservation statuses.
- **Admin Dashboard:** Access detailed statistics about library usage and manage user comments.
- **User Engagement:** Rate books and leave comments to share feedback with other readers.
- **Secure Access:** Role-based security ensuring only authorized users can access sensitive administrative features.
- **Interactive API Documentation:** Full OpenAPI/Swagger UI integration for exploring and testing REST endpoints.

## 🛠️ Technologies Used

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** (Authentication & Role-based Authorization)
- **Spring Data JPA** (Object-Relational Mapping)
- **Spring Web** (RESTful APIs & MVC)
- **Hibernate** (JPA implementation)
- **PostgreSQL** (Production Database)
- **H2** (In-memory Database for testing)
- **Maven** (Build and Dependency Management)

### Frontend
- **Thymeleaf** (Server-side Templating Engine)
- **HTML5 / CSS3 / JavaScript**
- **Bootstrap** (UI Styling)

### Testing & Quality
- **JUnit 5** (Unit & Integration Testing)
- **Selenium** (End-to-End Testing with Headless Chrome)
- **ArchUnit** (Architecture Verification)
- **JaCoCo** (Code Coverage Reporting)
- **Mockito** (Mocking framework)

### DevOps & Infrastructure
- **Docker** (Containerization for Database)
- **Docker Compose** (Service Orchestration)

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- [Java 17 JDK](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/)

## ⚙️ Getting Started

### 1. Setup Database
The project uses PostgreSQL. You can quickly start the database using Docker Compose:

```bash
docker-compose up -d
```

This will launch a PostgreSQL container with the following credentials (configured in `application.yml`):
- **Database:** `library_db`
- **User:** `nati`
- **Password:** `nati_pass`

### 2. Build and Run the Application
Use the Maven wrapper to build and start the Spring Boot application:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

## 📖 API Documentation

The project includes an interactive Swagger UI for exploring the REST API. Once the application is running, access it at:

`http://localhost:8080/swagger-ui/index.html`

## 🧪 Testing

The project emphasizes quality with multiple testing layers:

- **Unit & Integration Tests:** Run using `mvn test`.
- **Architecture Tests:** Located in `ug.project.library.architecture`, ensuring code follows architectural constraints using ArchUnit.
- **End-to-End (E2E) Tests:** Selenium-based tests located in `ug.project.library.e2e`.
- **Code Coverage:** JaCoCo reports are generated in `target/site/jacoco/index.html` after running tests.

To run all tests:
```bash
./mvnw test
```

## 📂 Project Structure

- `src/main/java`: Backend source code organized by layers (Controller, Service, Repository, Model, DTO, DAO).
- `src/main/resources/templates`: Thymeleaf templates for the web UI.
- `src/main/resources/static`: Static assets (CSS, JS, Images).
- `src/test/java`: Comprehensive test suites across all layers.
- `docker-compose.yml`: Configuration for containerized development infrastructure.

---
Developed as a showcase of modern Spring Boot development practices.
