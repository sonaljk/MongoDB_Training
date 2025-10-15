package com.training.finance.controller;

import com.training.finance.model.Transaction;
import com.training.finance.service.TransactionService;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.recordTransaction(transaction);
    }

    @GetMapping("/{accountId}")
    public List<Transaction> getTransactions(@PathVariable String accountId) {
        return transactionService.getTransactionsByAccount(accountId);
    }

    @GetMapping("/")
    public List<Transaction> getAccounts() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{accountId}/balance")
    public double getBalance(@PathVariable String accountId) {
        return transactionService.getBalance(accountId);
    }

    @GetMapping("/{type}/{amount}")
    public List<Transaction> getTransactionsByTypeGreaterThanAmount(@PathVariable String type, @PathVariable double amount) {
        return transactionService.getTransactionsByTypeGreaterThanAmount(type,amount);
    }

    @GetMapping("/stats-by-city")
    public List<Document> getSuccessTransactionStatsByCity() {
        return transactionService.getSuccessTransactionStatsByCity();
    }

    @GetMapping("/accounts/{city}")
    public List<Transaction> getTransactionsByCity(@PathVariable String city) {
        return transactionService.getTransactionsByCity(city);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String transactionId, @RequestBody Transaction transaction) {
        // Logic to update the transaction
        return transactionService.updateTransaction(transactionId, transaction)
                .map(updated -> ResponseEntity.ok(updated))      // 200 OK if updated
                .orElseGet(() -> ResponseEntity.notFound().build());  // 404 if not found
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransactionById(@PathVariable String transactionId) {
        boolean deleted = transactionService.deleteTransactionById(transactionId);
        if (deleted) {
            return ResponseEntity.noContent().build();  // 204
        } else {
            return ResponseEntity.notFound().build();   // 404
        }
    }


}
