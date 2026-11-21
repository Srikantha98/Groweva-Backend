<<<<<<< HEAD
# Evabirds Backend API Documentation

Welcome to the **Evabirds** backend service. This project is a production-grade, secure RESTful API built with **Spring Boot** and **PostgreSQL**, designed to support web (React.js) and mobile (Flutter) applications.

---

## Table of Contents
1. [Tech Stack](#tech-stack)
2. [Architecture & Project Structure](#architecture--project-structure)
3. [Configuration & Environment Setup](#configuration--environment-setup)
4. [Authentication & Security](#authentication--security)
5. [Complete API Reference](#complete-api-reference)
   - [System & Health](#1-system--health)
   - [Authentication (`/api/auth`)](#2-authentication-apiauth)
   - [User Management (`/api/users`)](#3-user-management-apiusers)
   - [User Profile (`/api/profiles`)](#4-user-profile-apiprofiles)
   - [Farms Management (`/api/farms`)](#5-farms-management-apifarms)
   - [Farm Images (`/api/farms/{farmId}/images`)](#6-farm-images-apifarmsfarmidimages)
   - [Favorites (`/api/favorites`)](#7-favorites-apifavorites)
   - [Direct Messaging (`/api/messages`)](#8-direct-messaging-apimessages)
6. [Enums Reference](#enums-reference)
7. [Frontend Integration Guide (React & Flutter)](#frontend-integration-guide)
8. [Error Handling](#error-handling)

---

## Tech Stack
- **Framework:** Spring Boot 4.1.1 (Spring WebMVC, Spring Security, Spring Data JPA, Spring Validation)
- **Language:** Java 17
- **Database:** PostgreSQL
- **Security:** JWT (JSON Web Tokens via JJWT 0.12.6) + BCrypt Password Encoding
- **Build Tool:** Apache Maven

---

## Architecture & Project Structure

```
src/main/java/org/evabirds/evabirds/
├── controller/         # REST Controllers exposing HTTP endpoints
│   ├── AuthController.java
│   ├── FarmController.java
│   ├── FarmImageController.java
│   ├── FavoriteController.java
│   ├── HomeController.java
│   ├── MessageController.java
│   ├── ProfileController.java
│   └── UserController.java
├── dto/                # Data Transfer Objects (Requests & Responses)
├── entity/             # JPA Entities mapped to PostgreSQL tables
│   ├── Farm.java
│   ├── FarmImage.java
│   ├── Favorite.java
│   ├── Message.java
│   ├── Profile.java
│   └── User.java
├── exception/          # GlobalExceptionHandler and Custom Exceptions
├── repository/         # Spring Data JPA Repositories
├── security/           # JWT Filter, JwtService, and SecurityConfig (CORS & Auth)
└── service/            # Core business logic implementations
```

---

## Configuration & Environment Setup

Configuration is located in `src/main/resources/application.properties`:

```properties
spring.application.name=Evabirds

# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/evabirds
spring.datasource.username=postgres
spring.datasource.password=YourDatabasePassword

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration-ms=86400000
```

### Running the Application Locally
- **Using Maven Wrapper:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **Or via IntelliJ IDEA:**
  Run the main application class `EvabirdsApplication.java`.

The server will start on port `8080` (default: `http://localhost:8080`).

---

## Authentication & Security

The backend implements stateless JWT authentication:
- **Sessions:** Stateless (`SessionCreationPolicy.STATELESS`)
- **CORS:** Pre-configured to allow cross-origin requests from web browsers (e.g., `http://localhost:3000`, `http://localhost:5173`) and mobile clients.
- **Header format for protected endpoints:**
  ```http
  Authorization: Bearer <your_jwt_token_here>
  ```

### Public vs Protected Endpoints Summary

| Endpoint | Method | Access |
| :--- | :--- | :--- |
| `/` | `GET` | **Public** |
| `/api/auth/**` | `POST` | **Public** |
| `/api/users/register`, `/api/users/login` | `POST` | **Public** |
| `/api/farms` | `GET` | **Public** |
| `/api/farms/{id}` | `GET` | **Public** |
| `/api/farms/{farmId}/images` | `GET` | **Public** |
| *All other endpoints* | Any | **Authenticated (Requires JWT)** |

---

## Complete API Reference

### 1. System & Health

#### Get System Status
- **Method:** `GET`
- **URL:** `/`
- **Auth:** Public
- **Response (200 OK):**
```json
{
  "application": "Evabirds API",
  "message": "Application is running successfully!",
  "status": "UP"
}
```

---

### 2. Authentication (`/api/auth`)

#### User Registration
- **Method:** `POST`
- **URL:** `/api/auth/register`
- **Auth:** Public
- **Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "phone": "+91 98421 88400"
}
```
*(Note: No role selection needed. Evabirds provides unified non-role-restricted accounts with default `ROLE_USER` permissions, allowing any registered user to both list farms for rent/lease and submit rent inquiries)*
- **Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "type": "Bearer",
  "userId": 1,
  "name": "John Doe",
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+91 98421 88400",
  "role": "ROLE_USER",
  "user": {
    "id": 1,
    "email": "john@example.com",
    "role": "ROLE_USER",
    "enabled": true,
    "fullName": "John Doe",
    "phone": "+91 98421 88400"
  }
}
```

#### User Login
- **Method:** `POST`
- **URL:** `/api/auth/login`
- **Auth:** Public
- **Request Body:**
```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```
- **Response (200 OK):** Same structure as Registration response with JWT token.

---

### 3. User Management (`/api/users`)

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/users/me` | Bearer | Get current authenticated user details |
| `GET` | `/api/users` | Bearer | Get list of all users |
| `GET` | `/api/users/{id}` | Bearer | Get user by ID |
| `DELETE` | `/api/users/{id}` | Bearer | Delete user by ID |
| `PUT` | `/api/users/change-password` | Bearer | Change current user password |
| `PUT` | `/api/users/update-email` | Bearer | Update email (returns fresh token) |

#### Change Password Request Body:
```json
{
  "oldPassword": "CurrentPassword123",
  "newPassword": "NewPassword123"
}
```

---

### 4. User Profile (`/api/profiles`)

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/profiles/me` | Bearer | Get current user's profile |
| `PUT` | `/api/profiles/me` | Bearer | Update current user's profile |
| `GET` | `/api/profiles/{userId}` | Bearer | Get profile by user ID |
| `GET` | `/api/profiles` | Bearer | Get all user profiles |

#### Update Profile Request Body:
```json
{
  "fullName": "John Doe",
  "phoneNumber": "+1234567890",
  "address": "123 Farm Road",
  "city": "Dallas",
  "state": "Texas",
  "country": "USA",
  "zipCode": "75001",
  "bio": "Experienced poultry farmer",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

---

### 5. Farms Management (`/api/farms`)

#### Create a Farm Listing
- **Method:** `POST`
- **URL:** `/api/farms`
- **Auth:** Bearer Token
- **Request Body:**
```json
{
  "title": "Green Acres Poultry Farm",
  "description": "Fully automated layer poultry farm with modern sheds.",
  "listingType": "SELL",
  "farmType": "BROILER",
  "capacity": 50000,
  "landSize": 5.5,
  "landUnit": "ACRES",
  "housingSystem": "CAGE_SYSTEM",
  "environmentType": "ENVIRONMENT_CONTROLLED",
  "hasElectricity": true,
  "hasWaterSource": true,
  "hasRoadAccess": true,
  "price": 1250000.00,
  "negotiable": true,
  "address": "45 Country Rd",
  "district": "Rural District",
  "state": "Texas",
  "country": "USA",
  "pincode": "75002",
  "latitude": 32.7767,
  "longitude": -96.7970
}
```
- **Response (201 Created):** Full `FarmResponse` object.

#### Get All Farms (with optional filters)
- **Method:** `GET`
- **URL:** `/api/farms?listingType=SELL&farmType=BROILER&status=ACTIVE`
- **Auth:** Public
- **Query Parameters (optional):**
  - `listingType` (`SELL`, `LEASE`, `BUY`)
  - `farmType` (`BROILER`, `LAYER`, `BREEDER`, `HATCHERY`, `INTEGRATED`)
  - `status` (`ACTIVE`, `PENDING`, `SOLD`, `LEASED`, `INACTIVE`)
- **Response (200 OK):** Array of `FarmResponse` objects.

#### Get Farm By ID
- **Method:** `GET`
- **URL:** `/api/farms/{id}`
- **Auth:** Public

#### Get My Farms
- **Method:** `GET`
- **URL:** `/api/farms/my-farms`
- **Auth:** Bearer Token

#### Update Farm
- **Method:** `PUT`
- **URL:** `/api/farms/{id}`
- **Auth:** Bearer Token (must be farm owner)

#### Update Farm Status
- **Method:** `PATCH`
- **URL:** `/api/farms/{id}/status?status=SOLD`
- **Auth:** Bearer Token (must be farm owner)

#### Delete Farm
- **Method:** `DELETE`
- **URL:** `/api/farms/{id}`
- **Auth:** Bearer Token (must be farm owner)
- **Response:** `204 No Content`

---

### 6. Farm Images (`/api/farms/{farmId}/images`)

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/farms/{farmId}/images` | Bearer | Add image URL to farm |
| `GET` | `/api/farms/{farmId}/images` | Public | Get all images for a farm |
| `PATCH`| `/api/farms/{farmId}/images/{imageId}/primary` | Bearer | Set image as primary |
| `DELETE`| `/api/farms/{farmId}/images/{imageId}` | Bearer | Delete farm image |

#### Add Image Request Body:
```json
{
  "imageUrl": "https://example.com/farm-shed.jpg",
  "isPrimary": true
}
```

---

### 7. Favorites (`/api/favorites`)

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/favorites/{farmId}` | Bearer | Toggle favorite (add/remove) |
| `GET` | `/api/favorites` | Bearer | Get all favorites for current user |
| `GET` | `/api/favorites/{farmId}/check` | Bearer | Check if farm is favorited (`{"isFavorite": true}`) |

---

### 8. Direct Messaging (`/api/messages`)

| Method | Endpoint | Auth | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/messages` | Bearer | Send a direct message |
| `GET` | `/api/messages/conversation/{otherUserId}` | Bearer | Get conversation with another user |
| `GET` | `/api/messages/inbox` | Bearer | Get inbox summaries of all chats |
| `GET` | `/api/messages/unread-count` | Bearer | Get unread message count |
| `PATCH`| `/api/messages/{messageId}/read` | Bearer | Mark a message as read |

#### Send Message Request Body:
```json
{
  "receiverId": 2,
  "farmId": 1,
  "content": "Hello, is this farm still available for lease?"
}
```

---

## Enums Reference

| Enum | Possible Values |
| :--- | :--- |
| **ListingType** | `SELL`, `LEASE`, `BUY` |
| **FarmType** | `BROILER`, `LAYER`, `BREEDER`, `HATCHERY`, `INTEGRATED` |
| **FarmStatus** | `ACTIVE`, `PENDING`, `SOLD`, `LEASED`, `INACTIVE` |
| **HousingSystem** | `DEEP_LITTER`, `CAGE_SYSTEM`, `SLAT_FLOOR`, `FREE_RANGE`, `OTHER` |
| **EnvironmentType**| `OPEN_SIDED`, `SEMI_CONTROLLED`, `ENVIRONMENT_CONTROLLED` |

---

## Frontend Integration Guide

### React.js (Axios Example)
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Automatically inject JWT Token into headers
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Example: Fetch all active farms
export const getFarms = async () => {
  const res = await api.get('/api/farms?status=ACTIVE');
  return res.data;
};
```

### Flutter (http Example)
```dart
import 'dart:convert';
import 'package:http/http.dart' as http;

// Note: Use 'http://10.0.2.2:8080' for Android Emulator
// Use 'http://localhost:8080' for iOS Simulator or Flutter Web
const String baseUrl = 'http://10.0.2.2:8080';

Future<List<dynamic>> fetchFarms() async {
  final response = await http.get(Uri.parse('$baseUrl/api/farms'));
  if (response.statusCode == 200) {
    return jsonDecode(response.body);
  }
  throw Exception('Failed to load farms');
}
```

---

## Error Handling

All error responses return standard JSON format managed by `GlobalExceptionHandler`:

```json
{
  "timestamp": "2026-10-09T18:40:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for argument...",
  "path": "/api/auth/register"
}
```

Common HTTP status codes returned:
- `200 OK` / `201 Created` / `204 No Content`: Successful requests
- `400 Bad Request`: Field validation errors
- `401 Unauthorized`: Bad credentials or invalid/expired token
- `403 Forbidden`: Action not permitted or missing authorization
- `404 Not Found`: Resource (User, Farm, Message) not found
=======
# Groweva-Backend
Evabirds backend
>>>>>>> origin/main
