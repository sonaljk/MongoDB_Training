package com.training.finance.repository;

import com.training.finance.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountNumber);

    boolean existsByTxnId(String transactionId);

    void deleteByTxnId(String transactionId);

    Optional<Transaction> findByTxnId(String id);

    List<Transaction> findByAddressCity(String city);
}