![mebelike](https://github.com/user-attachments/assets/4b546fa2-ea72-46aa-b0f2-a3cb84d9367f)
# Clinic Booking Microservices (Spring Boot)

This project is a cloud-ready microservices backend for a small clinic. It manages patients, doctors & time slots, and appointments across three Spring Boot services:
- **patient-service**
- **doctor-service**
- **appointment-service**

All traffic flows through a Spring Cloud Gateway, with Google OAuth2 (JWT) securing sensitive endpoints. The project ships with CI/CD via GitHub Actions (builds & pushes Docker images) and Docker Compose for local deployment.
Ready to explore how it’s structured and how to run it? Let’s dive in together.

## Made by
- **Enis Haliti**: https://github.com/EnisHaliti1 

---

## Architecture

- API Gateway (8085)
  Single entry point. Routes to the three services and enforces security:
    - Public: GET /doctors/**
    - Protected: everything else (/patients, /appointments, etc.)
    - Uses Google OAuth2.
- patient-service (8080, MongoDB)
  Stores patient records. Exposes CRUD-style endpoints (create, list, get by nationalId, update, delete).
- doctor-service (8083, MySQL)
  Stores doctors and their time slots. Exposes endpoints to fetch a doctor, list a doctor’s time slots, and reserve a time slot.
- appointment-service (8084, MySQL)
  Orchestrates bookings. Uses WebClient to:
    1. Fetch patient data from patient-service.
    2. Reserve a time slot in doctor-service.
    3. Persist the appointment locally if both steps succeed.
- Persistence
    - patient-service → MongoDB (mongo-patient)
    - doctor-service → MySQL (mysql-doctor)
    - appointment-service → MySQL (mysql-appointment)
- Configuration & Deployment
    - All services are containerized with Docker and wired together via docker-compose.
    - Cross-service base URLs and DB hosts/ports are injected through environment variables.
    - GitHub Actions builds and pushes images for each service on every push.
- Ports (host → container)
    - Gateway 8085 → 8085
    - patient-service 8080 → 8080 (Mongo 27017 → 27017)
    - doctor-service 8083 → 8083 (MySQL 3309 → 3306)
    - appointment-service 8084 → 8084 (MySQL 3310 → 3306)

### Tech Stack:
  - Java 17, Spring Boot 3, Spring WebFlux (WebClient)
  - Spring Cloud Gateway, OAuth2 Resource Server (Google JWT)
  - MongoDB, MySQL
  - Maven, JUnit 5, Mockito
  - Docker, Docker Compose
  - GitHub Actions (CI/CD for building & pushing images)

### Architecture Diagram
![Uploading DeploymentSchema.png…]()
**Figure:** Deployment diagram illustrating the request flow through the **API Gateway** to the three microservices and their databases.  
Key points:
- All clients (Postman / browser) call the **Gateway (8085)**.
- **Public routes**: `GET /doctors/**`. **Protected routes** (JWT via Google OAuth2): `/patients/**`, `/appointments/**`, etc.
- **patient-service** (8080) persists to **MongoDB**.
- **doctor-service** (8083) persists to **MySQL** and exposes **time slot reservation**.
- **appointment-service** (8084) orchestrates bookings using **WebClient** to call patient- and doctor-service, then stores the appointment in MySQL.
- Services are containerized and wired with **Docker Compose**; base URLs & DB settings are provided via environment variables.




POST /health
![image](https://github.com/user-attachments/assets/1ceadc80-5098-4695-b256-0f34b683111c)

PUT /health
![image](https://github.com/user-attachments/assets/a2c7a04e-ce8c-40e4-a348-59b65315782a)

