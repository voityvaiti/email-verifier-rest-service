# Email Verifier REST Service

## Overview
This is a demo project showcasing the usage of Spring Boot for building RESTful web services. It provides a RESTful API for email verification.

## Project Structure
- **Project Type:** Maven
- **Java Version:** 17
- **Framework:** Spring Boot 3.2.4

## Additional Notes
- **Packaging:** The application is packaged as a JAR file.
- **Artifact ID:** email-verifier-rest-service
- **Group ID:** com.myproject
- **Version:** 0.0.1-SNAPSHOT

## Requirements
- **Java Development Kit (JDK):** JDK 17 or later should be installed.
- **Maven:** Maven should be installed for building and managing the project dependencies.
- **Database:** The application requires PostgreSQL to be installed and running. The minimum supported version is PostgreSQL 15.3. 
- **IDE:** An Integrated Development Environment (IDE) such as IntelliJ IDEA, Eclipse, or NetBeans can be used for development.

## Running the Application
1. Clone the project from the repository.
2. Import the project into your preferred IDE.
3. Database configuration details such as URL, username, password, etc., should be provided in the `application.properties` file, or you can provide configuration in `application-*profile*.yml` file. Then profile that you used should be marked as active. Default profile: `local` (`application-local.yml` file).
4. Build the project using Maven: `mvn clean install`.
5. Run the application: `java -jar target/email-verifier-rest-service-0.0.1-SNAPSHOT.jar`. Or change packaging to `war` and deploy it to Tomcat Server.
6. Access the application endpoints using a web browser or API testing tool.

## Running the Application using Docker
1. Clone the project from the repository.
2. Build the project using Maven: `mvn clean install`.
3. Run docker-compose file: `docker-compose up`.
4. After start up, application will listen to `8080` host.
5. To shut down, execute `docker-compose down`.


# Endpoints

#### Default API prefix: `/api`

### Log In
- **URL:** `/{api-prefix}/auth/login`
- **Method:** `POST`
- **Security:** All permitted
- **Description:** Returns authentication token
- **Request Body:**
  ```json
  {
    "email": "string",
    "password": "string"
  }
- **Response:** Status code: `200 OK`
- **Response Body:**
  ```json
  {
    "token": "string"
  }

### Sign Up
- **URL:** `/{api-prefix}/api/auth/signup`
- **Method:** `POST`
- **Security:** All permitted
- **Description:** Registers a new user.
- **Request Body:**
  ```json
  {
    "email": "string",
    "password": "string"
  }
- **Response:** Status code: `201 CREATED`

### Resend Email Confirmation
- **URL:** `/{api-prefix}/auth/resend/email-confirmation/{email}`
- **Method:** `GET`
- **Security:** All permitted
- **Description:** Resends the email confirmation link.
- **Path Parameter:**
    - `email`: Email address of the user
- **Response:**
    - Status Code: `200 OK`

### Confirm Email
- **URL:** `/{api-prefix}/auth/email-confirm/{token}`
- **Method:** `GET`
- **Security:** All permitted
- **Description:** Confirms the email address using the provided token.
- **Path Parameter:**
    - `token`: Confirmation token
- **Response:**
    - Status Code: `200 OK`

### Send Reset Password Email
- **URL:** `/{api-prefix}/auth/send/reset-password-email/{email}`
- **Method:** `GET`
- **Security:** All permitted
- **Description:** Sends an email with token to reset the password.
- **Path Parameter:**
    - `email`: Email address of the user
- **Response:**
    - Status Code: `200 OK`

### Change Password
- **URL:** `/{api-prefix}/auth/change-password`
- **Method:** `POST`
- **Security:** All permitted
- **Description:** Changes the password of the current user.
- **Request Body:**
  ```json
  {
    "verificationToken": "string",
    "newPassword": "string"
  }
- **Response:** Status code: `200 OK`

### Ping
- **URL:** `/ping`
- **Method:** `GET`
- **Security:** All permitted
- **Description:** Ping endpoint for testing server availability.
- **Response:**
  - Status Code: `200 OK`
  - String `pong`

### Get Current User
- **URL:** `/{api-prefix}/user/current-user`
- **Method:** `GET`
- **Security:** Only authenticated
- **Description:** Retrieves information about the currently authenticated user.
- **Response:**
  - Status Code: `200 OK`
  - Response Body: UserResponseDto
    ```json
    {
      "id": "string",
      "email": "string",
      "enabled": "boolean",
      "roles": ["STRING"]
    }

### Get User Page
- **URL:** `/{api-prefix}/user/all`
- **Method:** `GET`
- **Security:** Only `ADMIN` authority
- **Description:** Retrieves a paginated list of users.
- **Query Parameters:**
  - `page-number` (optional): Page number (default: 0)
  - `page-size` (optional): Number of items per page (default: 10)
- **Response:**
  - Status Code: `200 OK`
  - Response Body: Page of UserResponseDto
    ```json
    {
      content [
        {
          "id": "string",
          "email": "string",
          "enabled": "boolean",
          "roles": ["STRING"]
        }, ...
      ],
      "pageable": {
        "pageNumber": "int",
        "pageSize": "int",
        "sort": {
            "empty": "boolean",
            "unsorted": "boolean",
            "sorted": "boolean"
        },
        "offset": "int",
        "unpaged": "boolean",
        "paged": "boolean"
      },
      "totalElements": "int",
      "totalPages": "int",
      "last": "boolean",
      "size": "int",
      "number": "int",
      "sort": {
        "empty": "boolean",
        "unsorted": "boolean",
        "sorted": "boolean"
      },
      "first": "boolean",
      "numberOfElements": "int",
      "empty": "boolean"
    }