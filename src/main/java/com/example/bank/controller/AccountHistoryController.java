package com.example.bank.controller;
import com.example.bank.dto.OrderResponseDTO;
import com.example.bank.dto.PaymentResponseDTO;
import com.example.bank.dto.sendToMainDTO;
import com.example.bank.model.Account;
import com.example.bank.model.AccountHistory;
import com.example.bank.service.AccountHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryController {
    private final AccountHistoryService accountHistoryService;

    @Qualifier("webClient8083")
    private final WebClient webClient;


    ///////////// 사업자 번호 입력 시 불러오기 (연결되면 accountId를 넘겨주고, 메인에서 그걸 business와 연결하기 )
    // 사업자 번호 가져오기 ------- 최초 1회
    @GetMapping("/check/account")
    public ResponseEntity<Account> checkAccount(
            @RequestParam String brNum) {
        Account connectedAccount = accountHistoryService.checkAccount(brNum);
        return ResponseEntity.ok(connectedAccount);
    }

    ///////////// 메인에게 accountHistory & account 보내기
    @PostMapping("/send/account")
    public ResponseEntity<sendToMainDTO> getAccountHistory() {
        try {
            // 연결된 Account 가져오기
            Account account = accountHistoryService.getConnectedAccount();
            // 해당 계좌의 거래 내역 가져오기
            List<AccountHistory> accountHistory = accountHistoryService.getConnectedAccountHistory(account.getAccountId());
            // DTO 생성
            sendToMainDTO response = new sendToMainDTO(account, accountHistory);

            // 응답 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in getAccountHistory: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /////////// pos
    // pos에서 출금 연결 (10초마다 pos랑 연결)
    // @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 */1 * * * *")
    public void withdraw(){
        // 외부 API 호출
        List<PaymentResponseDTO> request = (List<PaymentResponseDTO>) webClient.post()
                .uri("/api/payments/send")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PaymentResponseDTO>>() {})
                .block();

        if (request == null) {
            throw new IllegalArgumentException("외부 API로부터 데이터를 받을 수 없습니다.");
        }
        // 출금 처리
        log.info(accountHistoryService.withdraw(request));
    }

    // 입금 (10초마다 pos랑 연결)
    // @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 */1 * * * *")
    public void deposit() {
        // 외부 API 호출
        List<OrderResponseDTO> request = (List<OrderResponseDTO>) webClient.get()
                .uri("/api/orders/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<OrderResponseDTO>>() {})
                .block();

        if (request == null) {
            throw new IllegalArgumentException("외부 API로부터 데이터를 받을 수 없습니다.");
        }

        // 입금 처리
       log.info(accountHistoryService.deposit(request));

    }
}
