// 양동현. 2025.06.18
package com.example.pharmacy.prescription.entity;

import com.example.pharmacy.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
@Getter @Setter
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "doctor_name", nullable = false)
    private String doctorName;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.active;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionItem> prescriptionItems = new ArrayList<>();

    // 처방전 항목 추가
    public void addPrescriptionItem(PrescriptionItem item) {
        prescriptionItems.add(item);
        item.setPrescription(this);
    }

    // 처방전 유효성 확인
    public boolean isValid() {
        return !expiryDate.isBefore(LocalDate.now()) && status == PrescriptionStatus.active;
    }

    // 처방전 만료 여부
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    // 처방전 완전 소진 여부 확인
    public boolean isCompleted() {
        return prescriptionItems.stream()
                .allMatch(item -> item.getRemainingQuantity() <= 0);
    }

    // 처방전 상태 업데이트
    public void updateStatus() {
        if (isExpired()) {
            this.status = PrescriptionStatus.expired;
        } else if (isCompleted()) {
            this.status = PrescriptionStatus.completed;
        } else if (prescriptionItems.stream().anyMatch(item -> item.getRemainingQuantity() < item.getPrescribedQuantity())) {
            this.status = PrescriptionStatus.partially_used;
        }
    }
}