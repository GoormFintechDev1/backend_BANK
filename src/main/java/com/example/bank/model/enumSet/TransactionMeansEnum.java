package com.example.bank.model.enumSet;

public enum TransactionMeansEnum {
    CARD("CARD"),
    CASH("CASH");

    private final String means;

    TransactionMeansEnum(String Means) {
        this.means = Means;
    }
}
