// 양동현. 2025.06.18
package com.example.pharmacy.prescription.entity;

public enum PrescriptionStatus {
    active("활성"),
    partially_used("부분 사용"),
    completed("완료"),
    expired("만료");

    private final String displayName;

    PrescriptionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}