// 양동현. 2025.06.18
package com.example.pharmacy.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class InventoryDto {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private String medicineType;
    private String batchNumber;
    private LocalDate purchaseDate;
    private LocalDate expirationDate;
    private BigDecimal price;
    private Integer stockQuantity;
    private String supplierName;
    private Integer minimumStock;
    private boolean isExpirationImminent;
    private boolean isExpired;
    private boolean isLowStock;
}