package com.example.pharmacy.sales.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SaleItemFormDto {
    @NotNull(message = "약품은 필수입니다.")
    private Long medicineId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "최소 1개 이상 판매해야 합니다.")
    private Integer quantity;

    private Long prescriptionItemId; // 처방전 항목 ID (선택적)
}
