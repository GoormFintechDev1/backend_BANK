package com.example.bank.service;

import com.example.bank.dto.OrderResponseDTO;
import com.example.bank.dto.PaymentResponseDTO;
import com.example.bank.model.*;
import com.example.bank.model.enumSet.TransactionTypeEnum;
import com.example.bank.repository.AccountHistoryRepository;
import com.example.bank.repository.AccountRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryService {

    private final JPAQueryFactory queryFactory;
    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;
    private final WebClient webClient; // 외부 API 호출을 위한 WebClient

    private Account connectedAccount; // connect 메서드로 찾은 계좌를 저장

    public Account checkAccount(String brNum) {
        QAccount account = QAccount.account;

        // QueryDSL을 사용하여 brNum으로 계좌 검색
        connectedAccount = queryFactory.selectFrom(account)
                .where(account.brNum.eq(brNum))
                .fetchOne();

        if (connectedAccount == null) {
           log.info("계좌를 찾을 수 없습니다 : {}", brNum);
        }

        return connectedAccount;
    }


    // 계좌별 거래 내역 조회
    public List<AccountHistory> getConnectedAccountHistory(Long accountId) {
        QAccountHistory accountHistory = QAccountHistory.accountHistory;

        return queryFactory.selectFrom(accountHistory)
                .where(accountHistory.account.accountId.eq(accountId))
                .orderBy(accountHistory.transactionDate.desc()) // 거래일 내림차순 정렬
                .fetch();
    }

    // 기존에 작성된 getConnectedAccount 메서드 사용
    public Account getConnectedAccount() {
        if (connectedAccount == null) {
            throw new IllegalStateException("No account is connected.");
        }
        return connectedAccount;
    }

    @Transactional
    public String withdraw(List<PaymentResponseDTO> requests) {
        // 연결된 계좌 확인
        if (connectedAccount == null) {
            throw new IllegalStateException("먼저 계좌를 연결해야 합니다.");
        }

        BigDecimal totalWithdrawalAmount = BigDecimal.ZERO;

        for (PaymentResponseDTO request : requests) {
            // 잔액 확인
            BigDecimal balanceAmt = connectedAccount.getBalance();
            Long withdrawalAmount = request.getTotalAmount();

            if (balanceAmt.compareTo(BigDecimal.valueOf(withdrawalAmount)) < 0) {
                throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balanceAmt);
            }

            // 출금 내역 생성 및 저장
            AccountHistory accountHistory = new AccountHistory();
            accountHistory.setAccount(connectedAccount);
            accountHistory.setAmount(BigDecimal.valueOf(withdrawalAmount));
            accountHistory.setTransactionType(TransactionTypeEnum.valueOf("Expense")); // 거래 유형 설정
            accountHistory.setTransactionDate(LocalDateTime.now());
            accountHistoryRepository.save(accountHistory);

            // 잔액 차감
            BigDecimal updatedBalance = balanceAmt.subtract(BigDecimal.valueOf(withdrawalAmount));
            connectedAccount.setBalance(updatedBalance);

            totalWithdrawalAmount = totalWithdrawalAmount.add(BigDecimal.valueOf(withdrawalAmount));
        }

        // 변경된 계좌 저장
        accountRepository.save(connectedAccount);

        return "출금이 성공적으로 완료되었습니다. 총 출금 금액: " + totalWithdrawalAmount
                + "원, 현재 잔액: " + connectedAccount.getBalance() + "원";
    }


    // 입금
    @Transactional
    public String deposit(OrderResponseDTO request) {
        // 연결된 계좌 확인
        if (connectedAccount == null) {
            throw new IllegalStateException("먼저 계좌를 연결해야 합니다.");
        }

        // 입금 내역 생성 및 저장
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccount(connectedAccount);
        accountHistory.setTransactionType(TransactionTypeEnum.valueOf("Revenue"));
        accountHistory.setTransactionDate(request.getOrderDate());
        accountHistory.setAmount(BigDecimal.valueOf(request.getTotalPrice()));
        accountHistory.setStoreName(request.getProductName());


        accountHistoryRepository.save(accountHistory);


        // 잔액 추가
        BigDecimal balanceAmt = connectedAccount.getBalance();
        BigDecimal updatedBalance = balanceAmt.add(BigDecimal.valueOf(request.getTotalPrice()));
        connectedAccount.setBalance(updatedBalance);


        // 변경된 계좌 저장
        accountRepository.save(connectedAccount);

        return "송금이 성공적으로 완료되었습니다. 현재 잔액: " + updatedBalance;
    }

}