// 양동현. 2025.06.18
package com.example.pharmacy.sales.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class SaleItemDto {
    private Long id;
    private Long medicineId;
    private String medicineName;
    private String medicineType;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Boolean isPrescriptionRequired;
    private Long prescriptionItemId; // 처방전 항목 ID (없을 수 있음)
}
