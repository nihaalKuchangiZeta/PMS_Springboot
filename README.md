# PMS_Springboot

Spring Boot REST API for payment management system - Assessment Project

## Setup Instructions

### 1. Clone & Navigate
```bash
git clone https://github.com/your-username/PMS_Springboot.git
cd PMS_Springboot
```

### 2. Database Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payments_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Run Application
```bash
mvn clean install
mvn spring-boot:run
```
App runs on: `http://localhost:8080`

## Features

**User Management**
- Create users with roles
- List all users

**Payment Management**
- Create, read, update, delete payments
- Payment status tracking
- Payment categorization

## Project Structure
```
src/main/java/com/zeta/PMS/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── entity/         # Database models
├── dto/           # Request/Response objects
├── enums/         # Status, roles, categories
└── exception/     # Error handling
```

## Tech Stack
- Spring Boot
- MySQL
- JPA/Hibernate
- Maven
- JUnit (Testing)

## Testing
```bash
mvn test
```

Tests cover service and controller layers.
