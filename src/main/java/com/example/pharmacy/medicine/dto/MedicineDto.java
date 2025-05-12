package com.example.pharmacy.medicine.dto;

import com.example.pharmacy.medicine.entity.Medicine;
import com.example.pharmacy.medicine.entity.MedicineType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MedicineDto {

    private Long id;
    private String medicineName;
    private String manufacturerName;
    private MedicineType medicineType;
    private String description;
    private String effects;
    private Boolean prescriptionRequired;
    private Integer minimumStock;
    private Integer currentStock; // 현재 재고량 (medicine_inventory에서 계산)

    public static MedicineDto of(Medicine medicine) {
        MedicineDto medicineDto = new MedicineDto();
        medicineDto.setId(medicine.getId());
        medicineDto.setMedicineName(medicine.getMedicineName());
        medicineDto.setManufacturerName(medicine.getManufacturerName());
        medicineDto.setMedicineType(medicine.getMedicineType());
        medicineDto.setDescription(medicine.getDescription());
        medicineDto.setEffects(medicine.getEffects());
        medicineDto.setPrescriptionRequired(medicine.getPrescriptionRequired());
        medicineDto.setMinimumStock(medicine.getMinimumStock());
        return medicineDto;
    }
}