# MongoDB Lab Exercises – Spring Boot REST API

## Objective

In this lab, you will:

- Create a Spring Boot application.
- Connect to MongoDB.
- Build REST APIs for CRUD operations on the `transactions` collection in `finance_db`.
- Test the APIs using Swagger/OpenAPI endpoint.

---

## Prerequisites

- MongoDB server running locally (or Atlas cluster).
- Database: `finance_db`
- Collection: `transactions`
- Spring Boot starter project

### 1. **Add Mongodb dependency in pom.xml**

Open the finance-transactions-api starter project in intellij.

Add mongodb dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

---

### 2. **Add MongoDB Configuration**

Add the MongoDB connection details to the `application.properties`

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/finance_db
```

---

### 3. **Create the Entity Classes**

3.1. Create a `Address` class in the `model` package. This will be a field in Transaction class

```java
package com.training.finance.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String city;
    private String state;
    private String country;
}
```

3.2 Update the `Transaction` Entity

1. Update `Transaction` class in the `model` package to add all fields
2. Annotate it with `@Document` to map it to MongoDB transactions collection.

```java
package com.training.finance.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
public class Transaction {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String _id;
    private String txnId;
    private String accountId;
    private String type;
    private double amount;
    private String currency;
    private String status;
    private LocalDateTime date;
    private String channel;
    private String remarks;
    private Address address;
}

```

---

### 4. **Create the Repository**

1. Create a `TransactionRepository` interface in the `repository` package.
2. Extend `MongoRepository` to provide CRUD operations.

```java
package com.training.finance.repository;

import com.training.finance.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {

}
```

---

### 5. **Create the Service Layer**

1. Create a `TransactionService` class in the `service` package.
2. Add method `recordTransaction` to save a new transaction.

```java
package com.training.finance.service;

import com.training.finance.model.Transaction;
import com.training.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction recordTransaction(Transaction transaction) {
        return repository.save(transaction);
    }
}
```

---

### 6. **Update the Controller**

1. Update `TransactionController.java` to create an instance of `TransactinService`
2. Invoke `recordTransaction` from TransactionService to create a new transaction for Post request

Code snippet --

```java
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.recordTransaction(transaction);
    }
}
```

---

### 7. **Run the Application**

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints POST /api/transactions
4. Enter some details for various fields and hit Execute
5. Check that return status is 200 and connect to MongoDB compass to check that record is now added

---

### 8. **Return all transactions for a particular accountId**

8.1 Add method `getTransactionsByAccount` in `TransactionService` to return a list of transactions for the given account number

```java
public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return repository.findByAccountId(accountNumber);
    }
```

8.2 Add method `findByAccountId` to TransactionRepository

findByAccountId() is not a generic CRUD method and is not present in MongoRepository Interface.
Hence we need to declare it in TransactionRepository

```java
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountNumber);
}
```

8.3. Invoke the service method `getTransactionsByAccount` from the TransactionController class

```java
@GetMapping("/{accountId}")
    public List<Transaction> getTransactions(@PathVariable String accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }
```

8.4 Re-run the Application and test the endpoint

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints GET /api/transactions/{accountId}
4. Enter a valid accountId
5. Check that return status is 200 and check that records for the entered accountid are returned

---

### 9. **Return all transactions**

9.1 Add method `getAllTransactions` in `TransactionService` to return a list of transactions

```java
public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }
```

9.2 No changes to TransactionRepository

`findAll` method is present in the MongoRepository interface - so we don't need to declare it

9.3. Invoke the service method `getAllTransactions` from the TransactionController class

```java
@GetMapping("/")
    public List<Transaction> getAccounts() {
        return transactionService.getAllTransactions();
    }
```

9.4 Re-run the Application and test the endpoint

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints GET /api/transactions/
4. Check that return status is 200 and check that all records are returned

---

### 10. **Return balance for a particular accountnumber**

10.1 Add method `getBalance` in `TransactionService` to find all transactions for a accountnumber and calculate balance

```java
public double getBalance(String accountNumber) {
        return repository.findByAccountId(accountNumber)
                .stream()
                .mapToDouble(transaction -> transaction.getType().equals("Credit")? transaction.getAmount():transaction.getAmount()*-1)
                .sum();
}
```

10.2 No changes to TransactionRepository

`findByAccountId` method was already added

10.3. Invoke the service method `getBalance` from the TransactionController class

```java
@GetMapping("/{accountId}/balance")
    public double getBalance(@PathVariable String accountId) {
        return service.getBalance(accountId);
    }
```

10.4 Re-run the Application and test the endpoint

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints GET /api/transactions/{accountId}/balance
4. Check that return status is 200 and check that amount

---

### 11. **Delete a particular transaction**

11.1 Add method `deleteTransaction` in `TransactionService` to check if the particular transactionid exists and then delete it

```java
public boolean deleteTransactionById(String transactionId) {
        if (repository.existsByTxnId(transactionId)) {
            repository.deleteByTxnId(transactionId);
            return true;
        } else {
            return false;
        }
    }
```

11.2 Add methods to TransactionRepository

existsByTxnId() and deleteByTxnId() are not a generic CRUD methods and not present in MongoRepository Interface.
Hence we need to declare them in TransactionRepository

```java
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountNumber);
    boolean existsByTxnId(String transactionId);
    void deleteByTxnId(String transactionId);
}
```

11.3. Invoke the service method `deleteTransactionById` from the TransactionController class

```java
@DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable String transactionId) {
        boolean deleted = transactionService.deleteTransactionById(transactionId);
        if (deleted) {
            return ResponseEntity.noContent().build();  // 204
        } else {
            return ResponseEntity.notFound().build();   // 404
        }
    }
```

11.4 Re-run the Application and test the endpoint

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints DELETE /api/transactions/{transactionId}
4. Check that return status is 204 if successfully deleted or 404 when the transaction id is not found

---

### 12. **Update a particular transaction**

12.1 Add method `updateTransaction` in `TransactionService` to update if the particular transactionid exists

```java
public Optional<Transaction> updateTransaction(String id, Transaction updatedTransaction) {
        return repository.findByTxnId(id)
                .map(existing -> {
                    // Update only the desired fields
                    existing.setTxnId(updatedTransaction.getTxnId());
                    existing.setAccountId(updatedTransaction.getAccountId());
                    existing.setType(updatedTransaction.getType());
                    existing.setAmount(updatedTransaction.getAmount());
                    existing.setCurrency(updatedTransaction.getCurrency());
                    existing.setStatus(updatedTransaction.getStatus());
                    existing.setDate(updatedTransaction.getDate());
                    existing.setChannel(updatedTransaction.getChannel());
                    existing.setRemarks(updatedTransaction.getRemarks());
                    existing.setAddress(updatedTransaction.getAddress());
                    return repository.save(existing);
                    });
    }
```

12.2 Add method to TransactionRepository

findByTxnId() is not a generic CRUD methods and not present in MongoRepository Interface.
Hence we need to declare it in TransactionRepository

```java
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountNumber);
    boolean existsByTxnId(String transactionId);
    void deleteByTxnId(String transactionId);
    Optional<Transaction> findByTxnId(String id);
}
```

12.3. Invoke the service method `updateTransaction` from the TransactionController class

```java
@PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        // Logic to update the transaction
        return transactionService.updateTransaction(transactionId, transaction)
                .map(updated -> ResponseEntity.ok(updated))      // 200 OK if updated
                .orElseGet(() -> ResponseEntity.notFound().build());  // 404 if not found
    }
```

12.4 Re-run the Application and test the endpoint

1. Run the `FinanceApplication` class.
2. Open the OpenAPI endpoint - http://localhost:8080/swagger-ui/index.html
3. Test the endpoints PUT /api/transactions/{transactionId}
4. Check that return status is 200 if successfully updated or 404 when the transaction id is not found

---
