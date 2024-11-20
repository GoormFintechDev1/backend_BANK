package com.example.bank.service;
import com.example.bank.dto.DepositRequestDTO;
import com.example.bank.dto.WithdrawalRequestDTO;
import com.example.bank.model.Account;
import com.example.bank.model.AccountHistory;
import com.example.bank.model.QAccount;
import com.example.bank.model.QAccountHistory;
import com.example.bank.repository.AccountHistoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountHistoryService {
    private final JPAQueryFactory queryFactory;
    private final AccountHistoryRepository accountHistoryRepository;
    @Transactional
    ///// 출금
    public String withdraw(WithdrawalRequestDTO request) {
        // QueryDSL 객체
        QAccount qAccount = QAccount.account;
        QAccountHistory qAccountHistory = QAccountHistory.accountHistory;

        // 1. 계좌 조회
        Account account = queryFactory.selectFrom(qAccount)
                .where(qAccount.cntrAccountNum.eq(request.getAccountNumber()))
                .fetchOne();

        if (account == null) {
            throw new IllegalArgumentException("존재하지 않는 계좌 " + request.getAccountNumber());
        }

        // 2. 잔액 확인
        if (account.getBalanceAmt() < request.getAmount()) {
            throw new IllegalArgumentException("잔액 부족 " + request.getAccountNumber());
        }

        // 3. 잔액 차감
        account.setBalanceAmt(account.getBalanceAmt() - request.getAmount());

        // 거래 내역 생성
        AccountHistory history = new AccountHistory();
        history.setApiTranDtm(request.getApiTranDtm());
        history.setAccount(account);
        history.setTranDate(request.getApiTranDtm().substring(0, 8));
        history.setTranTime(request.getApiTranDtm().substring(8));
        history.setInoutType("출금");
        history.setTranAmt(request.getAmount());
        history.setAfterBalanceAmt(account.getBalanceAmt());
        history.setPrintContent(request.getPrintContent());
        history.setTranStatus("SUCCESS");
        history.setTranType(request.getTranType());

        // Repository를 통해 거래 내역 저장
        accountHistoryRepository.save(history);

        // 계좌 정보 업데이트
        queryFactory.update(QAccount.account)
                .where(QAccount.account.cntrAccountNum.eq(request.getAccountNumber()))
                .set(QAccount.account.balanceAmt, account.getBalanceAmt())
                .execute();

        return "출금 완료: " + request.getAccountNumber();
    }


    ///// 입금
    public String deposit(DepositRequestDTO request) {
       return null;
    }
}
