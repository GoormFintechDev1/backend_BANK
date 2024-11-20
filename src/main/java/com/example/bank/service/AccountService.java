package com.example.bank.service;

import com.example.bank.dto.AccountResponseDTO;
import com.example.bank.model.QAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final JPAQueryFactory queryFactory;

    // 계좌 정보 조회
    /*public AccountResponseDTO getAccountInfo(){
        QAccount qAccount = QAccount.account;

    }*/
}
