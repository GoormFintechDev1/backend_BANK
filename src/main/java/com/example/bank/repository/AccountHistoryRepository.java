package com.example.bank.repository;

import com.example.bank.model.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {
    // JpaRepository 기능 + Custom QueryDSL 기능 포함
}
