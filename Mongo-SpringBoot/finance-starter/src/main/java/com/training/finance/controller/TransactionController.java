package com.training.finance.controller;

import com.training.finance.model.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {


    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return new Transaction();
    }

    @GetMapping("/{accountId}")
    public List<Transaction> getTransactions(@PathVariable String accountId) {
        return Arrays.asList(new Transaction(), new Transaction());
    }

    @GetMapping("/")
    public List<Transaction> getAccounts() {
        return Arrays.asList(new Transaction(), new Transaction());
    }

    @GetMapping("/{type}/{amount}")
    public List<Transaction> getTransactionsByTypeGreaterThanAmount(@PathVariable String type, @PathVariable double amount) {
        return Arrays.asList(new Transaction(), new Transaction());
    }

    @GetMapping("/{accountId}/balance")
    public double getBalance(@PathVariable String accountId) {
        return 100;
    }

    @GetMapping("/stats-by-city")
    public List<Object> getSuccessTransactionStatsByCity() {
        return Arrays.asList(new Object(), new Object());
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        // Logic to update the transaction
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String transactionId) {
        // Logic to delete the transaction
        return ResponseEntity.notFound().build();
    }
}
