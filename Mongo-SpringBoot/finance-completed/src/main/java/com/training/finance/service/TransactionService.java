package com.training.finance.service;

import com.training.finance.model.Transaction;
import com.training.finance.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction recordTransaction(Transaction transaction) {
        return repository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return repository.findByAccountId(accountNumber);
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public double getBalance(String accountNumber) {
        return repository.findByAccountId(accountNumber)
                .stream()
                .mapToDouble(transaction -> transaction.getType().equals("Credit") ? transaction.getAmount() : transaction.getAmount() * -1)
                .sum();
    }

    public boolean deleteTransactionById(String transactionId) {
        if (repository.existsByTxnId(transactionId)) {
            repository.deleteByTxnId(transactionId);
            return true;
        } else {
            return false;
        }
    }

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

}