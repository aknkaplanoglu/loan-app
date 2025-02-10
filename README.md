# Loan Application API

This project is a simple loan management API for a bank. Employees can create loans, list loans, pay installments, and manage customers.

## Features

- Create Loan
- List Loans for a Customer
- List Installments for a Loan
- Create Customers
- Global Exception Handling with Custom Responses
- Docker Support
- H2 In-Memory Database

## Technologies Used

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Lombok
- Docker

## How to Run

1. Clone the repository:
    ```bash
    git clone <repository_url>
    cd loan-app
    ```

2. Build the application:
    ```bash
    mvn clean package
    ```

3. Run the application:
    ```bash
    mvn spring-boot:run
    ```

4. Access the API:
    - **Swagger UI (if integrated):** `http://localhost:8080/swagger-ui.html`
    - **H2 Console:** `http://localhost:8080/h2-console`

5. To build and run with Docker:
    ```bash
    docker build -t loan-app .
    docker run -p 8080:8080 loan-app
    ```

## API Endpoints

### **Customer Endpoints**
- `POST /api/customers`: Create a new customer.
- `GET /api/customers`: List all customers.
- `GET /api/customers/{id}`: Get customer by ID.

### **Loan Endpoints**
- `POST /api/loans`: Create a new loan.
- `GET /api/loans/{customerId}`: List loans for a customer.
- `GET /api/loans/installments/{loanId}`: List installments for a loan.

## Exception Handling

All exceptions return a consistent JSON format with `isSuccess`, `data`, and `errorMessage`.
