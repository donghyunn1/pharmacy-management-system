// 양동현. 2025.06.18
package com.example.pharmacy.medicine.entity;

public enum MedicineType {
    tablet("정제"),
    capsule("캡슐"),
    syrup("시럽"),
    injection("주사제"),
    ointment("연고"),
    other("기타");

    private final String displayName;

    MedicineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}