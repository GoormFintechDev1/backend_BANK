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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryService {

    private final JPAQueryFactory queryFactory;
    private final AccountRepository accountRepository;
    private final AccountHistoryRepository accountHistoryRepository;

    ///////////// 사업자 번호 입력 시 계좌 불러오기
    public Account checkAccount(String brNum) {
        QAccount account = QAccount.account;

        // QueryDSL을 사용하여 accountId가 1번인 계좌 고정
        Account connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌로 고정
                .fetchOne();

        if (connectedAccount == null) {
            log.info("계좌를 찾을 수 없습니다 : {}", brNum);
        }

        return connectedAccount;
    }

    // 연결된 계좌 가져오기 (accountId가 1번으로 고정)
    public Account getConnectedAccount() {
        QAccount account = QAccount.account;

        Account connectedAccount = queryFactory.selectFrom(account)
                .where(account.accountId.eq(1L)) // accountId가 1번인 계좌 고정
                .fetchOne();

        if (connectedAccount == null) {
            throw new IllegalStateException("accountId 1번 계좌가 존재하지 않습니다.");
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


    // 카테고리를 필터링하는 메서드
    private String filterCategoryByOrderName(String orderName) {
        if (orderName == null || orderName.isEmpty()) {
            return "기타"; // 기본 카테고리
        }

        orderName = orderName.toLowerCase(); // 소문자로 변환하여 비교

        // 공과금 관련 키워드
        if (orderName.contains("전기") || orderName.contains("가스") || orderName.contains("수도") || orderName.contains("통신") || orderName.contains("세금") || orderName.contains("공과금")) {
            return "공과금";
        }

        // 재료비 관련 키워드
        if (orderName.contains("원두") || orderName.contains("커피") || orderName.contains("재료") || orderName.contains("밀가루") || orderName.contains("설탕") || orderName.contains("베이킹") || orderName.contains("음료")) {
            return "재료비";
        }

        // 마케팅/광고 관련 키워드
        if (orderName.contains("광고") || orderName.contains("sns") || orderName.contains("마케팅") || orderName.contains("홍보")) {
            return "마케팅";
        }

        // 시설 관리 관련 키워드
        if (orderName.contains("수리") || orderName.contains("청소") || orderName.contains("에어컨") || orderName.contains("시설") || orderName.contains("관리")) {
            return "시설관리";
        }

        // 급여 관련 키워드
        if (orderName.contains("급여") || orderName.contains("월급") || orderName.contains("연봉") || orderName.contains("상여금")) {
            return "급여";
        }

        // 교통비 관련 키워드
        if (orderName.contains("택시") || orderName.contains("교통") || orderName.contains("버스") || orderName.contains("지하철")) {
            return "교통비";
        }

        // 식비 관련 키워드
        if (orderName.contains("식비") || orderName.contains("음식") || orderName.contains("저녁") || orderName.contains("점심") || orderName.contains("간식") || orderName.contains("식사")) {
            return "식비";
        }

        // 기타: 정의되지 않은 경우
        return "기타";
    }

    // 출금 처리
    @Transactional
    public String withdraw(List<PaymentResponseDTO> requests) {
        Account connectedAccount = getConnectedAccount();
        BigDecimal totalWithdrawalAmount = BigDecimal.ZERO;

        // DateTimeFormatter를 정의
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        for (PaymentResponseDTO request : requests) {
            String paymentKey = request.getPaymentKey();
            LocalDateTime transactionDate = LocalDateTime.parse(request.getRequestedAt(), formatter);
            BigDecimal withdrawalAmount = BigDecimal.valueOf(request.getTotalAmount());

            // 중복 데이터 확인
            boolean exists = queryFactory.selectOne()
                    .from(QAccountHistory.accountHistory)
                    .where(QAccountHistory.accountHistory.account.eq(connectedAccount)
                            .and(QAccountHistory.accountHistory.transactionDate.eq(transactionDate)))
                    .fetchFirst() != null;

            if (exists) {
                continue; // 중복된 거래는 처리하지 않음
            }

            // 잔액 확인
            BigDecimal balanceAmt = connectedAccount.getBalance();
            if (balanceAmt.compareTo(withdrawalAmount) < 0) {
                throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balanceAmt);
            }

            // 출금 내역 생성 및 저장
            AccountHistory accountHistory = AccountHistory.builder()
                    .account(connectedAccount)
                    .transactionType("EXPENSE")
                    .transactionMeans(TransactionMeansEnum.CARD.name())
                    .transactionDate(transactionDate)
                    .amount(withdrawalAmount)
                    .storeName(request.getProvider())
                    .note(request.getOrderName()) // 아이템 이름 order_name
                    .fixedExpenses(false)
                    .category(filterCategoryByOrderName(request.getOrderName())) // 카테고리
                    .build();
            accountHistoryRepository.save(accountHistory);

            // 잔액 차감
            BigDecimal updatedBalance = balanceAmt.subtract(withdrawalAmount);
            connectedAccount.setBalance(updatedBalance);
            totalWithdrawalAmount = totalWithdrawalAmount.add(withdrawalAmount);
        }

        accountRepository.save(connectedAccount);

        return "출금이 성공적으로 완료되었습니다. 총 출금 금액: " + totalWithdrawalAmount
                + "원, 현재 잔액: " + connectedAccount.getBalance() + "원";
    }

    // 입금 처리
    @Transactional
    public String deposit(List<OrderResponseDTO> requests) {
        Account connectedAccount = getConnectedAccount();
        BigDecimal totalDepositedAmount = BigDecimal.ZERO;

        for (OrderResponseDTO request : requests) {
            LocalDateTime transactionDate = request.getOrderDate();
            BigDecimal depositAmount = BigDecimal.valueOf(request.getTotalPrice());

            // 중복 데이터 확인
            boolean exists = queryFactory.selectOne()
                    .from(QAccountHistory.accountHistory)
                    .where(QAccountHistory.accountHistory.account.eq(connectedAccount)
                            .and(QAccountHistory.accountHistory.transactionDate.eq(transactionDate)))
                    .fetchFirst() != null;

            if (exists) {
                continue; // 중복된 거래는 처리하지 않음
            }

            // 입금 내역 생성 및 저장
            AccountHistory accountHistory = AccountHistory.builder()
                    .account(connectedAccount)
                    .transactionType("REVENUE")
                    .transactionMeans(TransactionMeansEnum.CARD.name())
                    .transactionDate(transactionDate)
                    .category("pos 입금")
                    .fixedExpenses(false)
                    .note("pos 입금")
                    .amount(depositAmount)
                    .storeName(request.getProductName())
                    .build();
            accountHistoryRepository.save(accountHistory);

            // 잔액 추가
            BigDecimal updatedBalance = connectedAccount.getBalance().add(depositAmount);
            connectedAccount.setBalance(updatedBalance);
            totalDepositedAmount = totalDepositedAmount.add(depositAmount);
        }

        accountRepository.save(connectedAccount);

        return "입금이 성공적으로 완료되었습니다. 총 입금 금액: " + totalDepositedAmount
                + "원, 현재 잔액: " + connectedAccount.getBalance() + "원";
    }
}
