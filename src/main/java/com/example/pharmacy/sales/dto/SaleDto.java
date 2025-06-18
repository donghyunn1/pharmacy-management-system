// 양동현. 2025.06.18
package com.example.pharmacy.sales.dto;

import com.example.pharmacy.sales.entity.PaymentMethod;
import com.example.pharmacy.sales.entity.SaleStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SaleDto {
    private Long id;
    private String customerName; // 고객 이름 (없을 수 있음)
    private Long customerId; // 고객 ID (없을 수 있음)
    private Long prescriptionId; // 처방전 ID (없을 수 있음)
    private String memberName; // 판매 담당 직원 이름
    private LocalDateTime saleDate;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private String paymentMethodDisplay;
    private SaleStatus status;
    private String statusDisplay;
    private List<SaleItemDto> saleItems = new ArrayList<>();
}
