package com.example.ewalletapplication.repository;

import com.example.ewalletapplication.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Transaction findByTransactionId(String transactionId);

    List<Transaction> findByFromUser(String fromUser);

    @Query(value = "select * from transaction where to_user=?1 and transaction_type='ADD'",nativeQuery = true)
    List<Transaction> getAddTransactionsOfUser(String userId);
}
