# Incident Management System

A web-based incident management application developed with **Spring Boot, Spring Security, JWT, and React**. The application allows employees to report IT incidents, technicians to manage assigned incidents, and administrators to supervise and assign incidents.

---

## Project Overview

The Incident Management System is a full-stack web application designed to simplify the reporting, assignment, and tracking of IT incidents within an organization.

The application implements role-based access control to ensure that each user can access only the functionalities permitted by their role.

Authentication is handled using **JSON Web Tokens (JWT)**, while Spring Security protects the backend REST API. The frontend, developed with React, communicates with the backend through HTTP requests using Axios.

The project demonstrates the implementation of a secure web application based on a layered architecture.

## Main Features

- User registration and login.
- JWT-based authentication.
- Role-based access control.
- Secure incident creation.
- Employee access to their own incidents.
- Administrator access to incident management.
- Assignment of incidents to technicians.
- Technician access to assigned incidents.
- Incident status updates.
- Protected REST API endpoints.
- Responsive user interface with React.

## User Roles

The application supports three user roles:

| Role | Permissions |
|---|---|
| EMPLOYEE | Register, log in, create incidents, and view personal incidents. |
| TECHNICIAN | Log in, view assigned incidents, and update their status. |
| ADMIN | View incidents, assign incidents to technicians, and access administrative incident operations. |

### Incident Workflow

The implemented incident workflow is:

1. An employee creates an incident.
2. The incident is initially assigned the `OPEN` status.
3. An administrator assigns the incident to a technician.
4. The technician updates the incident status to `IN_PROGRESS`.
5. The technician can mark the incident as `RESOLVED`.
6. The employee can consult the updated status.

**Implemented statuses:** `OPEN`, `IN_PROGRESS`, `RESOLVED`.

## Technology Stack

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- Maven
- H2 Database

### Frontend

- React
- JavaScript
- Axios
- Vite
- HTML5
- CSS3

### Development and Testing Tools

- IntelliJ IDEA
- Git and GitHub
- Postman
- Maven
- npm

## Application Architecture

The application follows a layered architecture:

```text
                    USER
                      |
                      v
             REACT FRONTEND
               (Port 5173)
                      |
                      v
               AXIOS CLIENT
                      |
               HTTP / JSON
             Authorization: Bearer JWT
                      |
                      v
             SPRING BOOT BACKEND
               (Port 8080)
                      |
                      v
              SPRING SECURITY
                      |
                      v
                 JWT FILTER
                      |
                      v
              REST CONTROLLERS
                /          \
               v            v
          AuthService   IncidentService
               \            /
                v          v
                 REPOSITORIES
                      |
                      v
                 H2 DATABASE
```

### Backend Layers

- **Controllers:** Handle HTTP requests and expose REST API endpoints.
- **Services:** Implement authentication and incident management business logic.
- **Repositories:** Handle database operations using Spring Data JPA.
- **Security:** Manage authentication, JWT validation, and role-based authorization.
- **Database:** H2 is used for development and testing.

## Project Structure

```text
JWT-Spring-Security-React/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ma/enset/incident_management/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── repository/
│   │   │   │       ├── security/
│   │   │   │       └── service/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── ...
│   ├── public/
│   └── package.json
│
├── screenshots/
│
├── .gitignore
└── README.md
```

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 17 or compatible JDK
- Maven
- Node.js and npm
- Git

### 1. Clone the repository

```bash
git clone https://github.com/houda-eljirari/JWT-Spring-Security-React.git
```

```bash
cd JWT-Spring-Security-React
```

### 2. Start the Backend

Navigate to the backend directory:

```bash
cd backend
```

Configure the JWT secret and activate the development profile.

**PowerShell:**

```powershell
$env:JWT_SECRET = "YOUR_BASE64_ENCODED_SECRET"
$env:SPRING_PROFILES_ACTIVE = "dev"
```

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend will be available at:

`http://localhost:8080`

### 3. Start the Frontend

Open another terminal and navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend will be available at:

`http://localhost:5173`

### 4. Access the H2 Database

The H2 console is available at:

`http://localhost:8080/h2-console`

Development database configuration:

| Property | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:incidentsdb` |
| Username | `sa` |
| Password | Leave empty |

**Important:** The project currently uses an in-memory H2 database. Data is reset when the backend application restarts.

## Test Accounts

The development profile initializes the following test accounts:

| Role | Username | Email | Password |
|---|---|---|---|
| ADMIN | `admin_test` | `admin_test@example.com` | `AdminTest123!` |
| TECHNICIAN | `technicien_test` | `technicien_test@example.com` | `TechnicienTest123!` |

An EMPLOYEE account can be created through the registration page.


## API Endpoints

The following endpoints are implemented in the backend.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new employee |
| POST | `/auth/login` | Authenticate a user and obtain a JWT |

### Incident Management

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/api/incidents` | Create an incident | Authenticated user |
| GET | `/api/incidents/my` | Retrieve personal incidents | EMPLOYEE |
| GET | `/api/incidents/{id}` | Retrieve an incident by ID | Authorized user |
| GET | `/api/incidents/assigned` | Retrieve assigned incidents | TECHNICIAN / ADMIN |
| PUT | `/api/incidents/{id}/status` | Update incident status | TECHNICIAN / ADMIN |
| PUT | `/api/incidents/{id}/assign` | Assign an incident to a technician | ADMIN |

## Application Screenshots

The following screenshots illustrate the main application interfaces and the incident management workflow.

### Authentication

**Login Page**

![Login Page](screenshots/01-login-page.png)

**Registration Page**

![Registration Page](screenshots/02-registration-page.png)

### Employee Interface

**Employee Dashboard**

![Employee Dashboard](screenshots/03-employee-dashboard.png)

**Create Incident**

![Create Incident](screenshots/04-create-incident.png)

**Employee Incident List**

![Employee Incident List](screenshots/05-employee-incident-list.png)

### Administrator Interface

**Admin Dashboard**

![Admin Dashboard](screenshots/06-admin-dashboard.png)

**Assign Technician**

![Assign Technician](screenshots/07-assign-technician.png)

### Technician Interface

**Technician Dashboard**

![Technician Dashboard](screenshots/08-technician-dashboard.png)

**Update Incident Status**

![Update Incident Status](screenshots/09-update-incident-status.png)

**Resolved Incident**

![Resolved Incident](screenshots/10-resolved-incident.png)

### Database and Build Verification

**H2 Database**

![H2 Database](screenshots/11-h2-database.png)

**Backend Tests**

![Backend Tests](screenshots/12-backend-tests.png)

**Frontend Build**

![Frontend Build](screenshots/13-frontend-build.png)

## Postman Tests

Postman was used to test the REST API and verify the main application workflows.

The tests cover:

- Employee registration.
- User login and JWT generation.
- Incident creation.
- Retrieval of personal incidents.
- Access to protected endpoints without a token.
- Administrator assignment of an incident to a technician.
- Retrieval of assigned incidents.
- Technician status updates.
- Verification of the resolved incident.

## Automated Tests

Backend tests are executed using Maven:

```bash
cd backend
mvn clean test
```

The previously verified backend test suite completed successfully:

- **11 tests**
- **0 failures**
- **0 errors**

Frontend production build:

```bash
cd frontend
npm run build
```

The frontend build was also successfully verified.

## Security

The application integrates several security mechanisms:

- JWT-based authentication.
- Stateless session management.
- Password encoding before database storage.
- Spring Security endpoint protection.
- Role-based authorization.
- Validation of JWT tokens on protected requests.
- Server-side access control for incident operations.
- CORS configuration for communication with the React frontend.
- Protection of administrative operations.
