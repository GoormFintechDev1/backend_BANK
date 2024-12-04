package com.example.bank.service;

import com.example.bank.dto.OrderResponseDTO;
import com.example.bank.dto.PaymentResponseDTO;
import com.example.bank.model.*;
import com.example.bank.model.enumSet.TransactionMeansEnum;
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
    private Account connectedAccount; // checkAccount 메서드로 찾은 계좌를 저장

    ///////////// 사업자 번호 입력 시 불러오기
    public Account checkAccount(String brNum) {
        QAccount account = QAccount.account;

        // QueryDSL을 사용하여 brNum으로 계좌 검색
        connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌로 고정
                .fetchOne();

        if (connectedAccount == null) {
           log.info("계좌를 찾을 수 없습니다 : {}", brNum);
        }

        return connectedAccount;
    }

    ///////////  메인에게 accountHistory & account 보내기
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
        QAccount account = QAccount.account;
        // QueryDSL을 사용하여 brNum으로 계좌 검색
        connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌로 고정
                .fetchOne();

        return connectedAccount;
    }

    /////////// pos
    @Transactional
    public String withdraw(List<PaymentResponseDTO> requests) {
        QAccount account = QAccount.account;
        // QueryDSL을 사용하여 brNum으로 계좌 검색
        connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌로 고정
                .fetchOne();


        BigDecimal totalWithdrawalAmount = BigDecimal.ZERO;

        for (PaymentResponseDTO request : requests) {
            // 중복 데이터 확인 (TransactionDate 기준)
            LocalDateTime transactionDate = LocalDateTime.parse(request.getRequestedAt());
            boolean exists = accountHistoryRepository.existsByAccountAndTransactionDate(
                    connectedAccount, transactionDate);
            if (exists) {
                log.info("이미 처리된 거래입니다. TransactionDate: {}", transactionDate);
                continue; // 중복된 거래는 처리하지 않음
            }

            // 잔액 확인
            BigDecimal balanceAmt = connectedAccount.getBalance();
            Long withdrawalAmount = request.getTotalAmount();

            if (balanceAmt.compareTo(BigDecimal.valueOf(withdrawalAmount)) < 0) {
                throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balanceAmt);
            }

            // 출금 내역 생성 및 저장
            AccountHistory accountHistory = AccountHistory.builder()
                    .account(connectedAccount)
                    .transactionType(String.valueOf(TransactionTypeEnum.EXPENSE))
                    .transactionMeans(String.valueOf(TransactionMeansEnum.CARD))
                    .transactionDate(transactionDate)
                    .amount(BigDecimal.valueOf(withdrawalAmount))
                    .storeName(request.getProvider())
                    .note("출금 처리")
                    .fixedExpenses(false)
                    .category("POS 출금")
                    .build();
            accountHistoryRepository.save(accountHistory);

            // 잔액 차감
            BigDecimal updatedBalance = balanceAmt.subtract(BigDecimal.valueOf(withdrawalAmount));
            connectedAccount.setBalance(updatedBalance);

            totalWithdrawalAmount = totalWithdrawalAmount.add(BigDecimal.valueOf(withdrawalAmount));
        }

        accountRepository.save(connectedAccount);

        return "출금이 성공적으로 완료되었습니다. 총 출금 금액: " + totalWithdrawalAmount
                + "원, 현재 잔액: " + connectedAccount.getBalance() + "원";
    }


    // 입금
    @Transactional
    public String deposit(List<OrderResponseDTO> requests) {
        // 연결된 계좌 확인
        QAccount account = QAccount.account;
        // QueryDSL을 사용하여 brNum으로 계좌 검색
        connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌로 고정
                .fetchOne();


        BigDecimal totalDepositedAmount = BigDecimal.ZERO; // 총 입금 금액 추적

        for (OrderResponseDTO request : requests) {

            // 출금 내역 생성 및 저장
            AccountHistory accountHistory = AccountHistory.builder()
                    .account(connectedAccount)
                    .transactionType(String.valueOf(TransactionTypeEnum.REVENUE))
                    .transactionMeans(String.valueOf(TransactionMeansEnum.CARD))
                    .transactionDate(request.getOrderDate())
                    .amount(BigDecimal.valueOf(request.getTotalPrice()))
                    .storeName(request.getProductName())
                    .build();

            accountHistoryRepository.save(accountHistory);

            // 잔액 추가
            BigDecimal balanceAmt = connectedAccount.getBalance();
            BigDecimal updatedBalance = balanceAmt.add(BigDecimal.valueOf(request.getTotalPrice()));
            connectedAccount.setBalance(updatedBalance);

            // 변경된 계좌 저장
            accountRepository.save(connectedAccount);

            totalDepositedAmount = totalDepositedAmount.add(BigDecimal.valueOf(request.getTotalPrice()));
        }

        return "입금이 성공적으로 완료되었습니다. 총 입금 금액: " + totalDepositedAmount + "원, 현재 잔액: " + connectedAccount.getBalance() + "원";
    }

}