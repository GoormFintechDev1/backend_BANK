package com.example.bank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bank_code")
public class BankCode {

    @Id
    @Column(name = "bank_code", length = 3, nullable = false, unique = true)
    private String bankCode; // 은행 코드 (Primary Key, 예: "001")

    @Column(name = "bank_name", length = 50, nullable = false)
    private String bankName; // 은행명 (예: "KB국민은행")

    @Column(name = "bank_alias", length = 20)
    private String bankAlias; // 은행 약어 또는 별칭 (예: "국민은행")
}
