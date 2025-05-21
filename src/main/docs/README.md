# UFO Sighting API

![UFO Illustration](img.png) A RESTful API for recording, retrieving, and managing UFO sighting reports. This application is built with Spring Boot and provides endpoints for querying sightings based on various criteria, submitting new sightings, and managing their status.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
    - [Database Setup (PostgreSQL)](#database-setup-postgresql)
- [Running the Application](#running-the-application)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
    - [Get All Sightings](#get-all-sightings)
    - [Get Sighting by ID](#get-sighting-by-id)
    - [Filter Sightings](#filter-sightings)
    - [Get Sightings in Geographical Bounds](#get-sightings-in-geographical-bounds)
    - [Create New Sighting](#create-new-sighting)
    - [Update Sighting Status](#update-sighting-status)
- [Data Loading](#data-loading)
- [Security](#security)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Features

* **CRUD Operations:** Create, Read, Update (status) UFO sightings.
* **Advanced Filtering:** Search sightings by shape, city, country, state, and free-text search within summaries.
* **Geospatial Queries:** Find sightings within specific geographical latitude/longitude bounds.
* **Pagination:** Support for paginated results on all listing endpoints.
* **Data Validation:** Input validation for creating and updating sightings.
* **Initial Data Loading:** Loads a predefined set of sightings from a JSON file on startup (if the database is empty).
* **Basic Security:** Secured endpoints for administrative actions like updating sighting status.
* **API Documentation:** Integrated Swagger/OpenAPI documentation.

## Technologies Used

* **Java 17**
* **Spring Boot 3.1.5**
    * Spring Web (for RESTful APIs)
    * Spring Data JPA (for database interaction)
    * Spring Security (for securing endpoints)
    * Spring Boot Validation (for request validation)
    * Spring Boot DevTools (for development convenience)
    * Spring Boot Actuator (for monitoring)
* **PostgreSQL:** Relational database for storing sighting data.
* **Hibernate:** JPA implementation.
* **Lombok:** To reduce boilerplate code.
* **Jackson:** For JSON serialization/deserialization (including Java 8 Date/Time support).
* **Springdoc OpenAPI:** For generating API documentation (Swagger UI).
* **Maven:** Dependency management and build tool.

## Prerequisites

* **JDK 17** or later installed.
* **Maven** installed.
* **PostgreSQL** server installed and running.
* An IDE like IntelliJ IDEA or Eclipse (optional, but recommended).

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd ufo-sighting-api
    ```

2.  **Database Setup (PostgreSQL):**
    * Ensure your PostgreSQL server is running.
    * Create a database named `ufo_sightings`.
        ```sql
        -- Connect to PostgreSQL (e.g., using psql)
        psql -U postgres 
        -- Then run:
        CREATE DATABASE ufo_sightings;
        ```
    * Verify that the user `postgres` has the password `postgres` and can connect to the `ufo_sightings` database. These are the default credentials in `application.properties`. If your PostgreSQL setup uses different credentials, update them in `src/main/resources/application.properties`.

3.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

## Running the Application

You can run the application using Maven or directly from your IDE:

* **Using Maven:**
    ```bash
    mvn spring-boot:run
    ```
* **From your IDE:**
    * Import the project as a Maven project.
    * Locate the `UfoSightingApiApplication.java` file in `src/main/java/com/ufomap/api/` and run it as a Java application.

The application will start by default on port `8080`.

## Configuration

Key application configurations are located in `src/main/resources/application.properties`:

* **Server Port:** `server.port=8080`
* **Database Connection:**
    * `spring.datasource.url=jdbc:postgresql://localhost:5432/ufo_sightings`
    * `spring.datasource.username=postgres`
    * `spring.datasource.password=postgres`
* **JPA/Hibernate:**
    * `spring.jpa.hibernate.ddl-auto=update` (Creates/updates schema based on entities)
    * `spring.jpa.show-sql=true` (Logs SQL queries)
* **Security (Basic Auth):**
    * `spring.security.user.name=admin`
    * `spring.security.user.password=password` (Used for secured endpoints)

Modify these properties as needed for your environment.

## API Endpoints

The API base path is `/api/sightings`. API documentation is available via Swagger UI at `http://localhost:8080/swagger-ui.html` when the application is running.

### Get All Sightings

* **Endpoint:** `GET /api/sightings`
* **Description:** Retrieves a paginated list of all UFO sightings.
* **Query Parameters (for pagination):**
    * `page`: Page number (default: 0)
    * `size`: Page size (default: 20)
    * `sort`: Property to sort by, e.g., `dateTime,desc`
* **Example:** `http://localhost:8080/api/sightings?page=0&size=10&sort=dateTime,desc`

### Get Sighting by ID

* **Endpoint:** `GET /api/sightings/{id}`
* **Description:** Retrieves a specific UFO sighting by its ID.
* **Example:** `http://localhost:8080/api/sightings/1`

### Filter Sightings

* **Endpoint:** `GET /api/sightings/filter`
* **Description:** Retrieves a paginated list of sightings based on filter criteria.
* **Query Parameters:**
    * `shape` (String, optional): e.g., "Triangle", "Circle"
    * `city` (String, optional): e.g., "Phoenix"
    * `country` (String, optional): e.g., "USA"
    * `state` (String, optional): e.g., "AZ"
    * `searchText` (String, optional): Free text search in city, state, country, summary, or shape.
    * `page`, `size`, `sort` (for pagination)
* **Example:** `http://localhost:8080/api/sightings/filter?shape=Triangle&city=Phoenix&page=0&size=5`

### Get Sightings in Geographical Bounds

* **Endpoint:** `GET /api/sightings/bounds`
* **Description:** Retrieves a paginated list of sightings within a specified geographical bounding box.
* **Query Parameters:**
    * `north` (Double, required): Northern latitude boundary.
    * `south` (Double, required): Southern latitude boundary.
    * `east` (Double, required): Eastern longitude boundary.
    * `west` (Double, required): Western longitude boundary.
    * `page`, `size`, `sort` (for pagination)
* **Example:** `http://localhost:8080/api/sightings/bounds?north=40.0&south=30.0&east=-100.0&west=-110.0`

### Create New Sighting

* **Endpoint:** `POST /api/sightings`
* **Description:** Submits a new UFO sighting.
* **Request Body (JSON):**
    ```json
    {
      "dateTime": "2024-05-21T10:00:00",
      "city": "Roswell",
      "state": "NM",
      "country": "USA",
      "shape": "Disk",
      "duration": "5 minutes",
      "summary": "Classic silver disk seen hovering.",
      "latitude": 33.3943,
      "longitude": -104.5230,
      "submittedBy": "eyewitness123"
    }
    ```
  *Note: `id`, `posted`, `submissionDate`, `isUserSubmitted`, and `submissionStatus` are typically set by the server.*
* **Response:** The created `SightingDTO` with HTTP status 201.

### Update Sighting Status

* **Endpoint:** `PATCH /api/sightings/{id}/status`
* **Description:** Updates the submission status of a specific sighting (e.g., "approved", "rejected", "pending").
* **Authentication:** Requires Basic Authentication (username: `admin`, password: `password` by default).
* **Path Variable:** `id` (Long): The ID of the sighting to update.
* **Query Parameter:**
    * `status` (String, required): The new status (e.g., "approved").
* **Example:** `PATCH http://localhost:8080/api/sightings/8/status?status=approved`
* **Response:** The updated `SightingDTO`.

## Data Loading

On application startup, if the `sightings` table in the database is empty, the `DataLoader` component (`src/main/java/com/ufomap/api/config/DataLoader.java`) will load initial data from `src/main/resources/data/sightings.json`. This behavior is active by default in non-production profiles (i.e., when the `prod` Spring profile is not active).

## Security

* Endpoints for retrieving sightings (`GET`) are generally public.
* The endpoint for updating sighting status (`PATCH /api/sightings/{id}/status`) is secured using Basic Authentication.
    * Default credentials (from `application.properties`):
        * Username: `admin`
        * Password: `password`
* CSRF protection is disabled for simplicity in this API, which is common for stateless REST APIs.
* CORS is configured to allow requests from any origin (`*`) for development purposes. This should be restricted in a production environment.

## Project Structure

ufo-sighting-api/├── .mvn/├── src/│   ├── main/│   │   ├── java/com/ufomap/api/│   │   │   ├── config/          # Configuration classes (e.g., DataLoader)│   │   │   ├── controller/      # REST API controllers│   │   │   ├── dto/             # Data Transfer Objects│   │   │   ├── exception/       # Custom exceptions and global exception handler│   │   │   ├── model/           # JPA entities and enums│   │   │   ├── repository/      # Spring Data JPA repositories│   │   │   ├── security/        # Spring Security configuration│   │   │   ├── service/         # Business logic services│   │   │   ├── sync/            # Update handling logic (Update.java, UpdateHandler.java)│   │   │   └── UfoSightingApiApplication.java # Main application class│   │   └── resources/│   │       ├── data/│   │       │   └── sightings.json # Initial sighting data│   │       ├── application.properties # Application configuration│   │       └── static/            # Static resources (if any)│   │       └── templates/         # View templates (if any)│   └── test/│       └── java/com/ufomap/api/ # Unit and integration tests├── pom.xml                        # Maven project configuration├── README.md                      # This file└── qodana.yaml                    # Qodana static analysis configuration
## Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix (`git checkout -b feature/your-feature-name`).
3.  Make your changes.
4.  Write tests for your changes.
5.  Ensure all tests pass (`mvn test`).
6.  Commit your changes (`git commit -m 'Add some feature'`).
7.  Push to the branch (`git push origin feature/your-feature-name`).
8.  Open a Pull Request.

## License

This project can be considered under the MIT License. See the `LICENSE` file for more details (if you choose to add one).

---

This README provides a good starting point. You can customize it further, especially the "Contributing" and "License" sections, and add any other specific details about your project.
