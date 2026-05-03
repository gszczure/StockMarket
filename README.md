# Stock Market Application

## Requirements

* Java 21
* Maven
* Docker

---

## Running application

Start the application using Docker:

```
docker-compose up --build
```

Application will be available at:

```
http://localhost:8080
```

---

## Tests

The project contains the following types of tests:

- Unit tests for service layer using JUnit and Mockito
- Controller tests using Spring MockMvc
- Integration tests using RestAssured

Integration tests run against a real PostgreSQL database using Testcontainers.

## Running tests

Run all tests:

```
mvn clean test
```

---

## API

### Get all stocks

```
GET /stocks
```

---

### Set stocks

```
POST /stocks
```

Example request:

```json
{
  "stocks": [
    { "name": "apple", "quantity": 5 }
  ]
}
```

---

### Buy stock

```
POST /wallets/{walletId}/stocks/{stockName}
```

```json
{
  "type": "BUY"
}
```

---

### Sell stock

```
POST /wallets/{walletId}/stocks/{stockName}
```

```json
{
  "type": "SELL"
}
```

---

### Get wallet

```
GET /wallets/{walletId}
```

---

### Get stock quantity in wallet

```
GET /wallets/{walletId}/stocks/{stockName}
```

---

### Get operation logs

```
GET /log
```

---

### Chaos endpoint (simulate instance failure)

```
POST /chaos
```

This endpoint terminates one application instance.

---

## High Availability

The application runs with two instances behind an nginx load balancer.

If one instance is stopped, the system continues to operate.

You can test this in two ways:

Stop one container manually:

```
docker stop app1
```

Or use chaos endpoint:

```
POST /chaos
```

The application will still respond using the remaining instance.

---

## Notes

* Uses PostgreSQL database
* Uses Docker for environment consistency
* Works on Windows, Linux and macOS
* Supports ARM64 and x64 architectures
* Does not require any environment configuration apart from Docker
