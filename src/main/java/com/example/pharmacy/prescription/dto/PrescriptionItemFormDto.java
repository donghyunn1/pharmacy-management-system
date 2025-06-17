package com.example.pharmacy.prescription.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PrescriptionItemFormDto {
    @NotNull(message = "약품은 필수입니다.")
    private Long medicineId;

    @NotNull(message = "처방 수량은 필수입니다.")
    @Min(value = 1, message = "처방 수량은 1개 이상이어야 합니다.")
    private Integer prescribedQuantity;
}