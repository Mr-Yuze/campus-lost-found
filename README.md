# Campus Lost & Found Management System

## 1. Project Description
The Campus Lost & Found Management System is a Spring Boot application that helps students and staff report lost and found items on campus. The system provides a centralized platform to log items, tracks status changes, matches lost and found reports, and includes role-based access control.

## 2. Technologies
- Java 27
- Spring Boot 4.0.0
- Maven
- Spring Web
- Spring Data JPA
- Spring Security with JWT (JSON Web Tokens)
- MySQL
- Jakarta Bean Validation

## 3. Database Setup
Ensure you have MySQL installed and running on port 3306.
Create the database:
```sql
CREATE DATABASE IF NOT EXISTS lost_found_db;
```

## 4. How to Run in IntelliJ
1. Open IntelliJ IDEA.
2. Select `File > Open` and choose the project directory (`campus-lost-found`).
3. Reload the Maven project to resolve dependencies.
4. Open `CampusLostFoundApplication.java`.
5. Click the green Run button.
6. The application will start on port `8080`.

## 5. MySQL Configuration
Ensure your `src/main/resources/application.properties` has the correct database configuration.
Change the username and password to your local MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lost_found_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

## 6. API List
- **Authentication**:
  - `POST /api/auth/register` - Register a new user
  - `POST /api/auth/login` - Login and get JWT token
- **Lost Reports**:
  - `POST /api/lost-reports` - Create a lost report
  - `GET /api/lost-reports` - Get all lost reports (supports filtering by category, status, location, keyword)
  - `GET /api/lost-reports/{id}` - Get lost report by ID
- **Found Items**:
  - `POST /api/found-items` - Create a found item
  - `GET /api/found-items` - Get all found items (supports filtering by category, status, location, keyword)
  - `GET /api/found-items/{id}` - Get found item by ID
  - `PUT /api/found-items/{id}/claim` - Mark a found item as CLAIMED
  - `PUT /api/found-items/{id}/return` - Mark a found item as RETURNED
- **Matches**:
  - `GET /api/matches` - Get all matches
  - `GET /api/matches/lost/{lostReportId}` - Get matches for a lost report
  - `GET /api/matches/found/{foundItemId}` - Get matches for a found item
- **Admin**:
  - `GET /api/admin/dashboard` - Get dashboard statistics

## 7. User Roles
- **STUDENT**: Can register, login, report lost items, view lost items, and search found items.
- **STAFF**: Can register, login, report lost items, report found items, claim and return found items they reported.
- **ADMIN**: Has access to all endpoints, can claim and return any found item, and can view the admin dashboard.

## 8. Authentication Instructions
This application uses JWT for security.
1. Send a POST request to `/api/auth/login` with your email and password.
2. The response will contain a `token`.
3. For all subsequent requests to protected endpoints, add the following header:
   `Authorization: Bearer <your_token>`

## 9. Postman Testing Instructions
A Postman collection `Campus-Lost-Found.postman_collection.json` is provided in the root directory.
1. Open Postman.
2. Click `Import` and select `Campus-Lost-Found.postman_collection.json`.
3. Set up the collection variables if needed (like tokens).
4. Run the requests in order (e.g., login first, copy token to variable, then make API requests).

## 10. Sample Accounts
The application creates sample data on the first run.
- **Admin**: Email: `admin@campus.edu`, Password: `admin123`
- **Student**: Email: `alice@student.campus.edu`, Password: `pass123`
- **Staff**: Email: `bob@staff.campus.edu`, Password: `pass123`

## 11. Business Rules
1. A found item **CANNOT** be marked `RETURNED` unless it was first marked `CLAIMED`.
2. Only an **ADMIN** or the **STAFF MEMBER** who originally reported the found item can mark it as `CLAIMED`.
3. Students cannot directly change arbitrary found-item statuses.
4. Invalid status transitions (e.g., `AVAILABLE -> RETURNED`, `RETURNED -> CLAIMED`) are strictly prevented at the service layer.

## 12. Example Requests/Responses

### Register
**Request**: `POST /api/auth/register`
```json
{
  "name": "Boomika",
  "email": "student@college.com",
  "password": "password123",
  "role": "STUDENT"
}
```
**Response**:
```json
{
  "success": true,
  "message": "Registration successful",
  "token": null,
  "user": null
}
```

### Login
**Request**: `POST /api/auth/login`
```json
{
  "email": "admin@campus.edu",
  "password": "admin123"
}
```
**Response**:
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI...",
  "user": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@campus.edu",
    "role": "ADMIN"
  }
}
```
