# Brokerage Firm Simulation - Spring Boot Application

This project is a Spring Boot application designed to simulate a brokerage firm. The application provides features like managing assets, orders, and simulating the initial public offering (IPO) for assets. This README will guide you through the setup, running the application, and using key features.

## Features
- Authentication and Authorization with JWT
- Admin and Customer roles (Admin can manage all orders, Customers can manage their own)
- Initial data generation (Admin user, customers, assets, and orders for IPO)
- Create, view, and cancel orders
- Order matching engine to execute BUY and SELL trades

## Prerequisites
- Java 11 or higher
- Maven
- A modern IDE (optional but recommended)

## Setup and Running the Application

1. **Clone the repository**
   ```sh
   git clone <repository-url>
   cd brokerage-firm-app
   ```

2. **Build the application**
   ```sh
   mvn clean install
   ```

3. **Run the application**
   ```sh
   mvn spring-boot:run
   ```

   Alternatively, you can build the JAR file and run it directly:
   ```sh
   mvn package
   java -jar target/brokerage-firm-app.jar
   ```

4. **Initial Data Setup**
   Upon running the application, the following users are created by default:
   - **Admin User**: Username: `admin`, Password: `123`
   - **Customer Users**:
     - Username: `customer1`, Password: `123`
     - Username: `customer2`, Password: `123`
     - Username: `customer3`, Password: `123`
   
   Additionally, 20 assets are created with initial values and random prices, simulating an IPO.

## Usage Instructions

### Register a New Customer
To register a new customer, use the following `curl` command:
```sh
curl --location 'http://localhost:8080/api/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "username": "customer5",
    "password": "123"
}'
```

### Login as a User
To login and obtain a JWT token, use the following `curl` command:
```sh
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "username": "customer3",
  "password": "123"
}'
```
This will return a JWT token, which you need to access secured endpoints.

### Create an Order
To create an order (e.g., buy an asset), use the following `curl` command, replacing `$TOKEN` with your JWT token:
```sh
curl --location 'http://localhost:8080/api/orders' \
--header 'Authorization: Bearer $TOKEN' \
--header 'Content-Type: application/json' \
--data '{
    "customerId": 4,
    "assetName": "Apple",
    "orderSide": "BUY",
    "size": 1500,
    "price": 50
}'
```
Replace `customerId`, `assetName`, `orderSide`, `size`, and `price` as needed.

### View Orders
To view a customer's orders, use the following `curl` command:
```sh
curl --location 'http://localhost:8080/api/orders?customerId=2&startDate=2024-11-01&endDate=2024-12-31' \
--header 'Authorization: Bearer $TOKEN' \
--header 'Content-Type: application/json'
```
Admin users can view all orders, while customers can view their own orders only.

### Cancel an Order
To cancel an order, use the following `curl` command:
```sh
curl --location --request DELETE 'http://localhost:8080/api/orders/20' \
--header 'Authorization: Bearer $TOKEN' \
--header 'Content-Type: application/json'
```
Admin users can cancel any order, while customers can only cancel their own.

## Postman Collection
A Postman JSON document is available for importing and testing all the endpoints. This will help you easily test the API without manually typing out requests.

## Notes and Improvements
- **Asset Pricing**: For simplicity, all initial asset prices are set in the system as TRY (Turkish Lira). In a real-world scenario, each asset should be assigned to a specific asset company account, and when selling during the initial public offering (IPO), the proceeds should be transferred to the respective company's account.
- **Withdrawals**: For withdrawals, it might be better to write some code to simulate sending money to a bank account based on an IBAN to make the simulation closer to real-world processes.
- **Bonus Feature Attempt**: Attempted to implement bonus feature 2 but couldn't complete due to insufficient understanding of the logic.
- **Testing**: Wrote unit tests for `OrderService`, but other services not have unit test due to time constraints.
- **Security**: JWT secrets are hardcoded in the code for simplicity, but in a real application, they should be read from environment variables or a secure config service.
- **Concurrency**: To improve the application's handling of concurrent requests, consider using optimistic locking and synchronous methods.
- **Work Timeline**: The project was developed over weekends and is intended to be production-ready or ready for extensive testing.

## Future Improvements
- Replace hardcoded secrets with environment variables.
- Improve concurrency handling with optimistic locking.
- Complete unit tests for all services.
- Implement realistic company account handling for IPO proceeds.
- Add functionality to simulate withdrawals to a bank account using IBAN.

## Disclaimer
This application is intended for production readiness testing and demonstration. It is suitable for a test environment but is not yet fully optimized for production use. Key security and scalability features may still require improvement.
