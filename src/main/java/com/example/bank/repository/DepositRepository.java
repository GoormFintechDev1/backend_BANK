package com.example.bank.repository;

import com.example.bank.model.Account;
import com.example.bank.model.DepositHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<DepositHistory, Long> {
}
