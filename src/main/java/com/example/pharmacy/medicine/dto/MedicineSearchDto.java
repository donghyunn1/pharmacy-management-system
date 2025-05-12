package com.example.pharmacy.medicine.dto;

import com.example.pharmacy.medicine.entity.MedicineType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MedicineSearchDto {

    private String searchQuery = "";
    private String searchBy = "medicineName"; // medicineName, manufacturerName, effects
    private MedicineType medicineType;
    private Boolean prescriptionRequired;
}
