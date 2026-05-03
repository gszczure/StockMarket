package org.example.stockmarket.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    BUY,
    SELL;

    @JsonCreator
    public static OperationType from(String value) {
        return OperationType.valueOf(value.toUpperCase());
    }
}
