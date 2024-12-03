package com.example.bank.service;

import com.example.bank.model.*;
import com.example.bank.repository.AccountRepository;
import com.example.bank.repository.DepositRepository;
import com.example.bank.repository.WithdrawalRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient webClient; // 외부 API 호출을 위한 WebClient

    private Account connectedAccount; // connect 메서드로 찾은 계좌를 저장

    /// 계좌 연결 확인
    @Transactional
    public boolean connect(String accountNum) {
        String apiServerUrl = "http://localhost:8080/api/account/info";

        try {
            // Step 1: API 호출 시작
            log.info("외부 API 호출 준비: {}", apiServerUrl); // URL 로그
            log.info("계좌 번호: {}", accountNum); // 입력된 계좌 번호 로그

            // Step 2: WebClient 설정
            WebClient webClient = WebClient.builder()
                    .baseUrl("http://localhost:8080")  // 기본 URL 설정
                    .build();
            log.info("WebClient 구성 완료");

            // Step 3: API 호출
            log.info("외부 API 호출 시작: {}?accountNum={}", apiServerUrl, accountNum); // 전체 URL 로그

            connectedAccount = webClient.get()
                    .uri(uriBuilder -> {
                        log.info("URI 빌더 실행"); // URI 빌더 실행 로그
                        return uriBuilder
                                .path("/api/account/info")  // 상대 경로 설정
                                .queryParam("accountNum", accountNum)  // 쿼리 파라미터 추가
                                .build();
                    })
                    .retrieve()
                    .bodyToMono(Account.class)
                    .doOnSubscribe(subscription -> log.info("외부 API 요청 구독 시작")) // 구독 시작 로그
                    .doOnNext(response -> log.info("외부 API 응답 수신: {}", response)) // 응답 수신 로그
                    .doOnError(error -> log.error("외부 API 호출 중 오류 발생: {}", error.getMessage())) // 오류 로그
                    .block();

            // Step 4: API 호출 성공
            if (connectedAccount != null) {
                log.info("외부 API 호출 성공: {}", connectedAccount); // 성공 메시지 추가
            } else {
                log.warn("외부 API 호출 성공했지만 결과가 null입니다.");
            }

            return true;

        } catch (Exception e) {
            // Step 5: 예외 발생 시 로그
            log.error("외부 API 호출 중 예외 발생: {}", e.getMessage(), e);
            throw new IllegalArgumentException("외부 API에서 계좌 정보를 가져올 수 없습니다.", e);
        } finally {
            // Step 6: 메서드 종료 로그
            log.info("connect 메서드 종료");
        }
    }

    // 출금
    @Transactional
    public String withdraw(WithdrawalRequestDTO request) {
        // 연결된 계좌 확인
        if (connectedAccount == null) {
            throw new IllegalStateException("먼저 계좌를 연결해야 합니다.");
        }

        // 잔액 확인
        BigDecimal balanceAmt = connectedAccount.getBalance();
        Long withdrawalAmount = request.getTotalAmount();
        if (balanceAmt.compareTo(BigDecimal.valueOf(withdrawalAmount)) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balanceAmt);
        }

        // 출금 내역 생성 및 저장
        WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
        withdrawalHistory.setAccount(connectedAccount);
        withdrawalHistory.setVat(request.getVat());
        withdrawalHistory.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now());
        withdrawalHistory.setApprovedAt(request.getApprovedAt() != null ? request.getApprovedAt() : String.valueOf(LocalDateTime.now()));
        withdrawalHistory.setCurrency(request.getCurrency());
        withdrawalHistory.setOrderName(request.getOrderName());

        withdrawalRepository.save(withdrawalHistory);

        // 잔액 차감
        BigDecimal updatedBalance = balanceAmt.subtract(BigDecimal.valueOf(withdrawalAmount));
        connectedAccount.setBalance(updatedBalance);



        // 변경된 계좌 저장
        accountRepository.save(connectedAccount);

        return "출금이 성공적으로 완료되었습니다. 현재 잔액: " + updatedBalance;
    }

    // 입금
    @Transactional
    public String deposit(DepositRequestDTO request) {
        // 연결된 계좌 확인
        if (connectedAccount == null) {
            throw new IllegalStateException("먼저 계좌를 연결해야 합니다.");
        }

        // 입금 내역 생성 및 저장
        DepositHistory depositHistory = new DepositHistory();
        depositHistory.setAccount(connectedAccount);
        depositHistory.setOrderId(request.getOrderId());
        depositHistory.setTotalPrice(request.getTotalPrice());
        depositHistory.setProductName(request.getProductName());
        depositHistory.setQuantity(request.getQuantity());
        depositHistory.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDateTime.now());
        depositHistory.setOrderStatus(request.getOrderStatus());
        depositHistory.setPaymentStatus(request.getPaymentStatus());

        depositRepository.save(depositHistory);


        // 잔액 추가
        BigDecimal balanceAmt = connectedAccount.getBalance();
        BigDecimal updatedBalance = balanceAmt.add(BigDecimal.valueOf(request.getTotalPrice()));
        connectedAccount.setBalance(updatedBalance);


        // 변경된 계좌 저장
        accountRepository.save(connectedAccount);

        return "송금이 성공적으로 완료되었습니다. 현재 잔액: " + updatedBalance;
    }

}