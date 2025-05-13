package com.example.pharmacy.inventory.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class InventoryFormDto {

    private Long id;

    @NotNull(message = "약품을 선택해주세요.")
    private Long medicineId;

    private String medicineName;

    @NotBlank(message = "배치 번호는 필수 입력값입니다.")
    private String batchNumber;

    @NotNull(message = "입고 날짜는 필수 입력값입니다.")
    private LocalDate purchaseDate;

    @NotNull(message = "유통기한은 필수 입력값입니다.")
    @Future(message = "유통기한은 미래 날짜여야 합니다.")
    private LocalDate expirationDate;

    @NotNull(message = "단가는 필수 입력값입니다.")
    @DecimalMin(value = "0.01", message = "단가는 0보다 커야 합니다.")
    private BigDecimal price;

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
    private Integer stockQuantity;

    @NotBlank(message = "공급사는 필수 입력값입니다.")
    private String supplierName;
}
