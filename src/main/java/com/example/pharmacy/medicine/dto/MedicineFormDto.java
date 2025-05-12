package com.example.pharmacy.medicine.dto;

import com.example.pharmacy.medicine.entity.MedicineType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineFormDto {

    private Long id;

    @NotBlank(message = "약품명은 필수 입력값입니다.")
    private String medicineName;

    @NotBlank(message = "제조사는 필수 입력값입니다.")
    private String manufacturerName;

    @NotNull(message = "약품 형태는 필수 입력값입니다.")
    private MedicineType medicineType;

    private String description;

    private String effects;

    private Boolean prescriptionRequired = false;

    @Min(value = 1, message = "최소 재고는 1 이상이어야 합니다.")
    private Integer minimumStock = 10;
}
