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
<img width="1031" height="738" alt="DeploymentSchema" src="https://github.com/user-attachments/assets/28acbaaf-4501-4ae5-a1c2-5eea15904c4f" />

**Figure:** Deployment diagram illustrating the request flow through the **API Gateway** to the three microservices and their databases.  
Key points:
- All clients (Postman / browser) call the **Gateway (8085)**.
- **Public routes**: `GET /doctors/**`. **Protected routes** (JWT via Google OAuth2): `/patients/**`, `/appointments/**`, etc.
- **patient-service** (8080) persists to **MongoDB**.
- **doctor-service** (8083) persists to **MySQL** and exposes **time slot reservation**.
- **appointment-service** (8084) orchestrates bookings using **WebClient** to call patient- and doctor-service, then stores the appointment in MySQL.
- Services are containerized and wired with **Docker Compose**; base URLs & DB settings are provided via environment variables.

--- 
## Services and Endpoints

### **All Endpoints**

#### Patient Service (protected)
- **POST** `/patients` -> Create patient
- **GET** `/patients?nationalId=BEL123` -> Get patient by nationalId
- **GET** `/patients/all` -> List all patients
- **PUT** `/patients/{nationalId}` -> Update patient
- **DELETE** `/patients/{nationalId}` -> Delete patient

#### Doctor Service
- **GET** `/doctors/{id}` -> Get doctor (💡 Public)
- **GET** `/doctors/{id}/timeslots` -> List time slots (💡 Public )
- **POST** `/timeslots/reserve` -> Reserve a time slot (Protected)

#### Appointment Service (protected)
- **POST** `/appointments` -> Book appointment
- **GET** `/appointments` -> List all appointments

---
## Docker Compose structure
<img width="863" height="568" alt="docker_compose_structure drawio" src="https://github.com/user-attachments/assets/1b46b5e1-b421-4bb1-b966-660027c0c66a" />

#### Databases and their volumes
<img width="1305" height="386" alt="databases drawio" src="https://github.com/user-attachments/assets/5a9c33ed-7b6b-4236-9cb7-7c7d71291dfb" />

**Services**
- mongo-patient (MongoDB) — persistent volume mongodb_patient_data, exposed on 27017.
- mysql-doctor (MySQL) — root pwd abc123, volume mysql_doctor_data, host port 3309 → 3306.
- mysql-appointment (MySQL) — root pwd abc123, volume mysql_appointment_data, host port 3310 → 3306.
- patient-service — uses MONGO_DB_HOST=mongo-patient, MONGO_DB_PORT=27017, exposed 8080.
- doctor-service — uses MYSQL_DB_HOST=mysql-doctor, MYSQL_DB_PORT=3306, exposed 8083.
- appointment-service — uses MYSQL_DB_HOST=mysql-appointment, plus PATIENT_SERVICE_BASEURL=patient-service:8080, DOCTOR_SERVICE_BASEURL=doctor-service:8083, exposed 8084.
- api-gateway — routes everything on 8085, with base URLs set to the internal service names
  
**Data persistence**
- Named volumes: mongodb_patient_data, mysql_doctor_data, mysql_appointment_data.

---

## Quick Start (Run Locally)

  **Prerequisites**
  - Docker & Docker Compose installed
  - Postman or curl for testing
  
1. **Clone & start**
  ```bash
    git clone https://github.com/EnisHaliti1/Advanced-Programming-Project.git
    cd Advanced-Programming-Project
    docker compose up -d
  ```
  Check containers: docker compose ps

  You should see: mongo-patient, mysql-doctor, mysql-appointment, patient-service, doctor-service, appointment-service, api-gateway

2. Ports overview
  ```bash
  - API Gateway: http://localhost:8085
  - patient-service: 8080 (behind gateway)
  - doctor-service: 8083 (behind gateway)
  - appointment-service: 8084 (behind gateway)
  - MongoDB: 27017, MySQL: 3309 (doctor), 3310 (appointment)
  ```

3. Smoke test (public routes)
<img width="927" height="412" alt="image" src="https://github.com/user-attachments/assets/9fcd3fe9-269b-4284-a69f-b02e1b4d5bc1" />
<img width="1283" height="797" alt="image" src="https://github.com/user-attachments/assets/cff08094-f55f-46da-87e5-06820928aeb0" />

4. Protected routes (JWT required)
   
  Most /patients/** and /appointments/** endpoints require a Google ID token:

  **Postman**

  - Authorization → Type: OAuth 2.0 → Get New Access Token
    <img width="1243" height="406" alt="image" src="https://github.com/user-attachments/assets/6e17be37-e7b3-4a9a-8c65-acec1df213de" />
    <img width="807" height="567" alt="image" src="https://github.com/user-attachments/assets/2d123f75-9b71-4889-9d0b-6e090e8eb4fd" />

      - Callback URL: https://www.getpostman.com/oauth2/callback
      - Auth URL: https://accounts.google.com/o/oauth2/auth
      - Access Token URL: https://oauth2.googleapis.com/token
      - Scope: openid profile email
      - Use the ID token as Bearer <id_token> (Authorization tab).

      Then click on:
    
    <img width="248" height="102" alt="image" src="https://github.com/user-attachments/assets/10def0da-79e7-459d-bba8-cb092ddd3e45" />
    


5. Authentication (Google OAuth2)

  This project secures non-public routes at the API Gateway using Google ID tokens (JWT). Public routes (e.g., GET /doctors/**) need no token; everything else requires Authorization

  - Configure Google OAuth2
    
    1. Create a GCP project in console.cloud.google.com
    2. OAuth consent screen → External → fill app info → Scopes: openid, profile, email → Test users: add your Google account.
    3. Credentials → Create Credentials → OAuth client ID → Web application.
      - Add authorized redirect URI: https://www.getpostman.com/oauth2/callback (for Postman).
    4. Copy your Client ID and Client Secret.
  
  - Gateway security

    SecurityConfig allows public: GET /doctors/**, and requires JWT for everything else.

  <img width="972" height="373" alt="image" src="https://github.com/user-attachments/assets/6989e3eb-b962-4db2-9d84-057e0c112ea4" />

  - Get an ID token in Postman (as shown above)
    
    Complete the Google sign-in. In the token popup, copy the ID Token (JWT). Click Use Token.
  
  - Call protected endpoints

  **Without authentication:**

<img width="1298" height="553" alt="image" src="https://github.com/user-attachments/assets/371d0a4f-01e9-42c8-a151-38bd48fcfa71" />
  
  Or try and delete a patient:

<img width="1327" height="566" alt="image" src="https://github.com/user-attachments/assets/09752d70-1efb-4184-b3c6-1e309e0b7097" />


  **With authentication:**

<img width="570" height="303" alt="image" src="https://github.com/user-attachments/assets/aa5f6615-9131-4893-8692-ff72ce71bda3" />

<img width="1317" height="868" alt="image" src="https://github.com/user-attachments/assets/8e7c17ba-e711-4129-ac98-ba4c85532bb8" />

  Let's go ahead and delete now the patient that we wanted to delete:

<img width="1297" height="587" alt="image" src="https://github.com/user-attachments/assets/ba08b0e2-2036-45ee-9302-13b8f3161adc" />

  And now the patient is deleted.

---

## Postman endpoints screenshots

  ### **Patient**

  POST /patients
  
  <img width="1312" height="567" alt="image" src="https://github.com/user-attachments/assets/db84069c-62ef-4f31-bc8c-71022339cfd3" />


  GET /patients/all

  <img width="1326" height="912" alt="image" src="https://github.com/user-attachments/assets/07d91b32-8960-4eb4-ab55-6b65e077e0a9" />


  GET /patients/?nationalId

  <img width="1315" height="922" alt="image" src="https://github.com/user-attachments/assets/b33a135d-c9fe-469c-a61b-2b6f675677f5" />


  PUT /patients/{nationalId}

  <img width="1312" height="928" alt="image" src="https://github.com/user-attachments/assets/7352dc2e-b8b0-42e2-98b1-6977c2f255cd" />
  <img width="1333" height="801" alt="image" src="https://github.com/user-attachments/assets/7a16caa6-8b07-4074-8fa8-bddbd0357936" />


  DELETE /patients/{nationalId}

  <img width="1327" height="921" alt="image" src="https://github.com/user-attachments/assets/1329cfc2-d4fd-4e6a-8c13-06d733d32fba" />

  Here we can see that the patient with nationalId "MICH123" is deleted

  <img width="1352" height="708" alt="image" src="https://github.com/user-attachments/assets/f4e14879-e39a-40d0-9bfb-f717652441a6" />


  ### **Doctor**

  
  GET /doctors/{id}

  <img width="1387" height="923" alt="image" src="https://github.com/user-attachments/assets/b2a65ab5-2d60-4521-bc5b-5094c930ff88" />


  GET /doctors/{id}/timeslots

  <img width="1371" height="928" alt="image" src="https://github.com/user-attachments/assets/e9aba495-ba18-43dc-bb77-f4b9f5f80bcd" />


  POST /timeslots/reserve

  <img width="1367" height="932" alt="image" src="https://github.com/user-attachments/assets/2c958d5e-4c6f-4462-bd7c-8ac523705f9d" />



  ### **Appointment**

  POST /appointments

  If you attempt to book an appointment for a time slot that is already reserved (e.g., timeslotId = 2), the booking is rejected.

  The doctor-service reports the slot is already reserved, and the appointment-service returns:

  ```bash
    Appointment booking failed
  ```

  <img width="1375" height="928" alt="image" src="https://github.com/user-attachments/assets/f0661476-fef3-4e4f-b0f4-daedf20b884e" />

  Conversely, when you select a time slot with status AVAILABLE, the booking succeeds, and the appointment-service responds:

  ```bash
    Appointment booked successfully
    (I’ll demonstrate the successful booking flow live during the presentation.)
  ```
  

  GET /appointments

  <img width="1373" height="931" alt="image" src="https://github.com/user-attachments/assets/e6c3b333-3ff0-4810-bdc8-a776564016e4" />
