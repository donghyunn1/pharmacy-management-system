package com.example.pharmacy.sales.dto;

import com.example.pharmacy.sales.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SaleFormDto {
    private Long customerId; // (선택적)
    private Long prescriptionId; // (선택적)

    @NotNull(message = "결제 방법은 필수입니다.")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "최소 하나 이상의 판매 항목이 필요합니다.")
    @Valid
    private List<SaleItemFormDto> saleItems = new ArrayList<>();
}
