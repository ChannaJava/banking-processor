# Banking Transaction Processor Microservice

A Spring Boot microservice for processing multi-account banking transactions, managing ledger balances, enforcing financial domain rules, and providing audit history.

---

## Features

* **Domain-Driven Architecture**: Encapsulates core banking business rules (overdraft prevention, positive amount validation, self-transfer prevention) directly inside the `Account` aggregate root.
* **Thread-Safe Balances**: Prevents concurrent race conditions during transactions using JPA Optimistic Locking (`@Version`).
* **Immutable Audit Ledger**: Records all deposits, withdrawals, and incoming/outgoing transfers with high-precision timestamps.
* **Data Validation**: Enforces input constraints at the controller level using Jakarta Bean Validation (`@NotNull`, `@Positive`, `@PositiveOrZero`).
* **Automated Exception Handling**: Provides standardized RESTful error payloads (`400 Bad Request`, `404 Not Found`).

---

## Tech Stack

* **Java**: 17 / 21
* **Framework**: Spring Boot 3.2.x (Spring Web, Spring Data JPA, Spring Validation)
* **Database**: In-Memory H2 Database
* **Testing**: JUnit 5, Spring Boot Test
* **Build Tool**: Apache Maven

---

## Getting Started

### Prerequisites

* **JDK**: Version 17 or higher
* **Maven**: 3.8+ (or use the Maven Wrapper)
* **IDE**: IntelliJ IDEA / Eclipse / VS Code

### Installation & Run

1. **Clone the repository:**
  
   git clone [https://github.com/ChannaJava/banking-processor.git]
   cd banking-processor


**Steps to fallow to run the project**

1.Build the project:

mvn clean install

2.Run application:
mvn spring-boot:run

3.The service will start on http://localhost:8081 (or 8080 if configured).

REST API Documentation
1. Create Account
   Endpoint: POST /api/v1/accounts

Request Body:

{
"initialBalance": 1000.00
}

Response (201 Created):
"a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d"

2. Get Account Balance
   Endpoint: GET /api/v1/accounts/{id}/balance

Response (200 OK):

{
"amount": 1000.00
}

3. Deposit Funds
   Endpoint: POST /api/v1/accounts/{id}/deposit

Request Body:
{
"amount": 500.00
}

Response (200 OK): Empty body / 200 OK

4. Withdraw Funds
   Endpoint: POST /api/v1/accounts/{id}/withdraw

Request Body:

{
"amount": 200.00
}

Response (200 OK): Empty body / 200 OK

5. Transfer Funds
   Endpoint: POST /api/v1/accounts/{id}/transfer

Request Body:

{
"targetAccountId": "f47ac10b-58cc-4372-a567-0e02b2c3d4e5",
"amount": 300.00
}

Response (200 OK): Empty body / 200 O

6. Get Transaction History (Ledger)
   Endpoint: GET /api/v1/accounts/{id}/history

Response (200 OK):

[
{
"id": "c1a2b3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
"type": "DEPOSIT",
"amount": { "amount": 1000.00 },
"timestamp": "2026-08-12T14:10:00Z",
"description": "Initial Balance"
},
{
"id": "d2e3f4a1-b2c3-4d5e-6f7a-8b9c0d1e2f3a",
"type": "TRANSFER_OUT",
"amount": { "amount": 300.00 },
"timestamp": "2026-08-12T14:15:00Z",
"description": "Transfer sent to account: f47ac10b-58cc-4372-a567-0e02b2c3d4e5"
}
]

Testing
Unit & Integration Tests
Run the automated JUnit test suite using Maven:

mvn test



Error Handling Matrix

HTTP Status                         Trigger ConditionSample                                                               Message

400 Bad Request	                   Insufficient balance / Overdraft attempt	                                            "Insufficient funds. Available: 100.00, Required: 500.00

400 Bad Request	                    Negative or zero transaction amount	                                                "Amount must be strictly greater than zero"

400 Bad Request	                      Attempting self-transfer	                                                           "Cannot transfer funds to the same account"

404 Not Found	                      Requesting a non-existent account UUID	                                            "Account not found: <UUID>"

