package com.example.pharmacy.inventory.entity;

import com.example.pharmacy.medicine.entity.Medicine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "medicine_inventory")
@Getter @Setter
public class MedicineInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "supplier_name")
    private String supplierName;

    // 유통기한 임박 확인 메서드
    public boolean isExpirationImminent() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return expirationDate.isBefore(thirtyDaysFromNow);
    }

    // 유통기한 만료 확인 메서드
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDate.now());
    }
}
