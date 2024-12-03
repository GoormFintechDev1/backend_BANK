package com.example.bank.controller;
import com.example.bank.service.AccountHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;



@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryController {
    private final AccountHistoryService accountHistoryService;
    private final WebClient webClient;

    // 계좌번호로 계좌 연결하기 (더블리에 있는 account와 연결)
    @GetMapping("/connect")
    public ResponseEntity<String> fetchAccountFromExternalApi(
            @RequestParam String accountNum
    ){
        boolean success = accountHistoryService.connect(accountNum);
        if(success){
            return ResponseEntity.ok("계좌 연결 성공");
        } else {
            return ResponseEntity.ok("계좌 연결 실패");
        }
    }

    // 출금 (10초마다 pos랑 연결)
    @Scheduled(fixedRate = 10000)
    public void withdraw(){
        // 외부 API 호출
        String externalApiUrl = "http://localhost:8083/api/payments/send";
        WithdrawalRequestDTO request = webClient.post()
                .uri(externalApiUrl)
                .retrieve()
                .bodyToMono(WithdrawalRequestDTO.class)
                .block();

        if (request == null) {
            throw new IllegalArgumentException("외부 API로부터 데이터를 받을 수 없습니다.");
        }

        // 출금 처리
        log.info(accountHistoryService.withdraw(request));
    }

    // 입금 (10초마다 pos랑 연결)
    @Scheduled(fixedRate = 10000)
    public void deposit() {
        // 외부 API 호출
        String externalApiUrl = "http://localhost:8083/api/orders/send";
        DepositRequestDTO request = webClient.post()
                .uri(externalApiUrl)
                .retrieve()
                .bodyToMono(DepositRequestDTO.class)
                .block();

        if (request == null) {
            throw new IllegalArgumentException("외부 API로부터 데이터를 받을 수 없습니다.");
        }

        // 입금 처리
       log.info(accountHistoryService.deposit(request));

    }
}
