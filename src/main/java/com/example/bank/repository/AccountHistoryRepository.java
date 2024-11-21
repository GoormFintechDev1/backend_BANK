package com.example.bank.repository;

import com.example.bank.model.WithdrawalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHistoryRepository extends JpaRepository<WithdrawalHistory, Long> {
}
