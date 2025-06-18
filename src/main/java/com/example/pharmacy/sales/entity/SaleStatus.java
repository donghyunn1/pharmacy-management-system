// 양동현. 2025.06.18
package com.example.pharmacy.sales.entity;

public enum SaleStatus {
    completed("완료"),
    cancelled("취소");

    private final String displayName;

    SaleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
