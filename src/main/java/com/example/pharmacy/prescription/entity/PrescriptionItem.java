// PrescriptionItem.java
package com.example.pharmacy.prescription.entity;

import com.example.pharmacy.medicine.entity.Medicine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "prescription_items")
@Getter @Setter
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "prescribed_quantity", nullable = false)
    private Integer prescribedQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    // 수량 소진 처리
    public void consumeQuantity(int quantity) {
        if (quantity > remainingQuantity) {
            throw new IllegalArgumentException("처방 수량을 초과했습니다.");
        }
        this.remainingQuantity -= quantity;
    }

    // 수량 복원 (판매 취소 시)
    public void restoreQuantity(int quantity) {
        this.remainingQuantity = Math.min(this.remainingQuantity + quantity, this.prescribedQuantity);
    }

    // 완전 소진 여부
    public boolean isCompleted() {
        return remainingQuantity <= 0;
    }
}