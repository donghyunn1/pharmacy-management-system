// 양동현. 2025.06.18
package com.example.pharmacy.prescription.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PrescriptionFormDto {
    private Long id;

    @NotNull(message = "고객은 필수입니다.")
    private Long customerId;

    @NotBlank(message = "의사명은 필수입니다.")
    private String doctorName;

    @NotBlank(message = "병원명은 필수입니다.")
    private String hospitalName;

    @NotNull(message = "발급일자는 필수입니다.")
    private LocalDate issueDate;

    @NotNull(message = "유효기간은 필수입니다.")
    @Future(message = "유효기간은 미래 날짜여야 합니다.")
    private LocalDate expiryDate;

    @Valid
    private List<PrescriptionItemFormDto> prescriptionItems = new ArrayList<>();
}