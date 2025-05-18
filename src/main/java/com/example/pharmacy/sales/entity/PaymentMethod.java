package com.example.pharmacy.sales.entity;

public enum PaymentMethod {
    cash("현금"),
    card("신용카드");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
