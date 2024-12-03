package com.example.bank.model.enumSet;

public enum TransactionTypeEnum {
    REVENUE("REVENUE"),
    EXPENSE("EXPENSE");

    private final String value;

    TransactionTypeEnum(String value) {
        this.value = value;
    }

}
