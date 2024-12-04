package com.example.bank.repository;

import com.example.bank.model.Account;
import com.example.bank.model.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

    boolean existsByAccountAndTransactionDate(Account connectedAccount, LocalDateTime transactionDate);
}
