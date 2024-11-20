package com.example.bank.service;

import com.example.bank.dto.AccountResponseDTO;
import com.example.bank.model.Account;
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
    public AccountResponseDTO getAccountInfo(String accountNumber) {
        QAccount qAccount = QAccount.account;

        // 계좌 정보 조회
        Account account = queryFactory.selectFrom(qAccount)
                .where(qAccount.cntrAccountNum.eq(accountNumber))
                .fetchOne();

        if (account == null) {
            throw new IllegalArgumentException("해당 계좌를 찾을 수 없습니다: " + accountNumber);
        }

        // DTO로 매핑하여 반환
        return new AccountResponseDTO(
                account.getCntrAccountNum(),
                account.getAccountHolderName(),
                account.getBankCode().getBankCode(),
                account.getBankCode().getBankName(),
                account.getBalanceAmt(),
                account.getAccountStatus(),
                account.getLastTransactionTime()
        );
    }
}
