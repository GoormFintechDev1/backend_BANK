package com.example.bank.service;

import com.example.bank.dto.DepositRequestDTO;
import com.example.bank.dto.WithdrawalRequestDTO;
import com.example.bank.model.*;

import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.DepositRepository;
import com.example.bank.repository.WithdrawalRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryService {

    private final JPAQueryFactory queryFactory;
    private final WithdrawalRepository withdrawalRepository;
    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    @Transactional
    public String withdraw(WithdrawalRequestDTO request, String accountNum) {
        QAccount qAccount = QAccount.account;

        // 1. 계좌 조회
        Account account = queryFactory
                .selectFrom(qAccount)
                .where(qAccount.accountNum.eq(accountNum))
                .fetchOne();

        if (account == null) {
            throw new IllegalArgumentException("해당 계좌를 찾을 수 없습니다.");
        }

        // 2. 잔액 확인 (withdrawal_card의 amount랑 account의 balanceAmt랑 비교)
        BigDecimal balanceAmt = account.getBalanceAmt();
        BigDecimal withdrawalAmount = request.getCard().getAmount();

        if (balanceAmt.compareTo(withdrawalAmount) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 3. 잔액 차감 (account 업데이트)
        BigDecimal updatedBalance = balanceAmt.subtract(withdrawalAmount);
        account.setBalanceAmt(updatedBalance);

        // 4. 출금 내역 생성 및 저장
        WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
        withdrawalHistory.setAccount(account);
        withdrawalHistory.setMId(request.getMId());
        withdrawalHistory.setCurrency("KRW");
        withdrawalHistory.setMethod(request.getMethod());
        withdrawalHistory.setApprovedAt(OffsetDateTime.now());
        withdrawalHistory.setSoldDate(request.getSoldDate());
        withdrawalHistory.setPaidOutDate(request.getPaidOutDate());
        withdrawalHistory.setCard(request.getCard());

        // 5. 수수료 저장 및 연결
        List<Withdrawal_fee> fees = new ArrayList<>();
        for (Withdrawal_fee fee : request.getFees()) {
            Withdrawal_fee managedFee = fee.getId() != null
                    ? entityManager.merge(fee) // 이미 존재하는 수수료는 merge
                    : fee; // 새로운 수수료는 그대로 사용
            fees.add(managedFee);
        }
        withdrawalHistory.setFees(fees);

        withdrawalRepository.save(withdrawalHistory);

        // 5. 변경된 계좌 저장
        accountRepository.save(account);

        // 6. 성공 메시지 반환
        return "출금이 성공적으로 완료되었습니다.";
    }


    @Transactional
    public String deposit(DepositRequestDTO request, String accountNum) {
        QAccount qAccount = QAccount.account;

        // 1. 계좌 조회
        Account account = queryFactory
                .selectFrom(qAccount)
                .where(qAccount.accountNum.eq(accountNum))
                .fetchOne();

        if (account == null) {
            throw new IllegalArgumentException("해당 계좌를 찾을 수 없습니다.");
        }

        // 2. 잔액 추가 (account 업데이트)
        BigDecimal balanceAmt = account.getBalanceAmt();
        BigDecimal updatedBalance = balanceAmt.add(request.getAmount());
        account.setBalanceAmt(updatedBalance);

        // 4. 출금 내역 생성 및 저장
        DepositHistory depositHistory = new DepositHistory();
        depositHistory.setAccount(account);
        depositHistory.setAmount(request.getAmount());

        depositRepository.save(depositHistory);

        // 5. 변경된 계좌 저장
        accountRepository.save(account);

        // 6. 성공 메시지 반환
        return "송금이 성공적으로 완료되었습니다.";
    }
}