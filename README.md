# Loan Application API

This project is a simple loan management API for a bank. Employees can create loans, list loans, pay installments, and manage customers.

## Features

- **Role-Based Access Control (RBAC):**
   - ADMIN can manage all customers.
   - CUSTOMER can only manage their own loans.
- **Create Loan:** Create a loan for a customer with a specified amount, interest rate, and installment plan.
- **List Loans:** View all loans for a customer.
- **List Installments:** View installment details of a loan.
- **Pay Loan Installments:** Pay loan installments according to business rules:
   - Installments must be paid in full, partial payments are not allowed.
   - Payments are applied to the earliest due installments first.
   - Payments can be made up to 3 months in advance.
   - **Bonus Feature: Early Payment Discount & Late Payment Penalty**
      - Early payment discount: `installmentAmount * 0.001 * (days before due date)`.
      - Late payment penalty: `installmentAmount * 0.001 * (days after due date)`.
   - The response includes the number of installments paid, total amount paid, and whether the loan is fully paid.
- **Global Exception Handling with Custom Responses:** All errors are returned in a standard format.
- **Security:** Basic authentication with role-based authorization.
- **Docker Support:** The application can be run in a Docker container.
- **H2 In-Memory Database:** Used for development and testing purposes.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database**
- **Lombok**
- **Docker**

---

## How to Run

### 1️⃣ Clone the Repository
```bash
git clone <repository_url>
cd loan-app
```

### 2️⃣ Build the Application
```bash
mvn clean package
```

### 3️⃣ Run the Application
```bash
mvn spring-boot:run
```

### 4️⃣ Access the API
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/h2-console`

### 5️⃣ Build and Run with Docker
```bash
docker build -t loan-app .
docker run -p 8080:8080 loan-app
```

---

## API Endpoints

### **Customer Endpoints (ADMIN Only)**
- `POST /api/customers` → Create a new customer.
- `GET /api/customers` → List all customers.
- `GET /api/customers/{id}` → Get customer by ID.

### **Loan Endpoints**
- `POST /api/loans` → Create a new loan.
- `GET /api/loans/{customerId}` → List loans for a customer (ADMIN only).
- `GET /api/loans/installments/{loanId}` → List installments for a loan.

### **Loan Payment Endpoints**
- `POST /api/loans/pay` → Pay installments for a loan.
   - **Query Parameters:**
      - `loanId` (Long): The loan ID for which the payment is being made.
      - `paymentAmount` (Double): The amount to be paid.
   - **Response Example:**
     ```json
     {
       "isSuccess": true,
       "data": {
         "paidInstallmentsCount": 2,
         "totalPaidAmount": 1000.0,
         "isLoanFullyPaid": false
       },
       "errorMessage": null
     }
     ```

---

## Exception Handling

All exceptions return a consistent JSON format:

```json
{
  "isSuccess": false,
  "data": null,
  "errorMessage": "Error message goes here"
}
```
## NOTES TO RUN

- Please register and login first
- please get the token after login and use it afterwards in each request auth header
- you can find postman collection under /collection folder.


---
