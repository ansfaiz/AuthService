# 🔐 AuthService: Secure & Scalable Authentication Microservice

## Introduction

This repository contains the source code for the **AuthService**, a dedicated microservice built on **Spring Boot**. Its primary role is to handle all user authentication, authorization, and token management, providing a secure foundation for all connected services within a larger system.

By centralizing authentication, this service ensures consistency, compliance, and scalability across the entire application ecosystem.

## ✨ Features

The AuthService is designed to provide a full suite of authentication capabilities:

* **User Registration:** Secure sign-up with password hashing.
* **User Login:** Authenticates credentials and issues a **JSON Web Token (JWT)**.
* **Token Validation:** API endpoint to validate incoming JWTs for protected routes.
* **Token Refresh:** (Optional) Mechanism to issue new access tokens using a refresh token.
* **Role-Based Access Control (RBAC):** (If applicable) Support for defining and checking user roles.
* **[auth_db]:** Persistent storage for user and role data.

## 🛠️ Technologies Used

This project leverages the power and maturity of the Java ecosystem:

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Backend** | **Java 17+** | Core programming language. |
| **Framework** | **Spring Boot [VERSION, e.g., 3.x]** | Rapid application development framework. |
| **Security** | **Spring Security** | Declarative security framework. |
| **Tokenization** | **JSON Web Tokens (JWT)** | Stateless authentication mechanism. |
| **Database** | **[MYSQL]** | Persistent storage for user data. |
| **Build Tool** | **[ Gradle]** | Dependency management and build automation. |

## 🚀 Getting Started

Follow these steps to set up and run the project locally.

### Prerequisites

You need the following software installed on your machine:

* **Java Development Kit (JDK) 17 or higher**
* **[Gradle]** build tool
* A running instance of **[auth_db]**

### Installation and Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ansfaiz/AuthService.git](https://github.com/ansfaiz/AuthService.git)
    cd AuthService
    ```

2.  **Configure Environment Variables:**
    Create an `application.properties`  (if it doesn't exist) and configure your database and JWT secret:

    ```properties
    # Database Configuration (Example for PostgreSQL)
    spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
    spring.datasource.username=[YOUR_DB_USERNAME]
    spring.datasource.password=[YOUR_DB_PASSWORD]
    
    # JWT Configuration
    jwt.secret=[A_LONG_AND_SECURE_SECRET_STRING_AT_LEAST_32_CHARS]
    jwt.expiration-ms=[86400000] # Example: 24 hours
    ```
    *Replace the placeholders with your actual secrets and connection details.*

3.  **Build and Run:**
    Use your build tool to compile the project and start the server:
    * **Using Gradle:**
        ```bash
        ./gradlew build
        java -jar build/libs/AuthService-0.0.1-SNAPSHOT.jar
        ```

The service will start on `http://localhost:[8080]` (or the port specified in your configuration).

## 💡 API Endpoints

The following are the primary endpoints exposed by the AuthService:

| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/v1/signup` | Creates a new user account and return refreshToken with token. |
| `POST` | `/auth/v1/login` | Authenticates user and returns a JWT. |
| `GET` | `/auth/v1/refreshToken` | Validates a JWT and returns refreshToken. |
 

### Example Request (Login)

**Request:** `POST` `http://localhost:8080/auth/v1/login`

```json
{
    "username": "user.name",
    "password": "strongpassword123"
}
```
Successful Response (200 OK):


**signUp-request
```JSON
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 101,
    "username": "user.name",
    "roles": ["ROLE_USER"]
}
```



## 🤝 Contribution

Contributions are welcome! If you find a bug or have a feature request, please open an issue or submit a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'feat: Add some amazing feature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License

Distributed under the **Apache 2.0** License. See the `LICENSE` file for more information.

---

## 📧 Contact

**faiyaz Ansari**
* **GitHub:** [@ansfaiz](https://github.com/ansfaiz)
* **Contact/Portfolio:** **[LINKEDIN ID -https://linkedin.com/in/md-faiyaz-ansari-6719212a4]**
