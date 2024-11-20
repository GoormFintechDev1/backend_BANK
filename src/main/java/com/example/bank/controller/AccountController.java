package com.example.bank.controller;

import com.example.bank.dto.AccountResponseDTO;
import com.example.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    // 계좌 정보 조회
    @GetMapping("/info")
    public ResponseEntity<AccountResponseDTO> getAccountInfo() {
        //AccountResponseDTO accountResponseDTO = accountService.getAccountInfo();
        return ResponseEntity.ok(null);

    }
}
