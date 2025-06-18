// 양동현. 2025.06.18
package com.example.pharmacy.prescription.dto;

import com.example.pharmacy.prescription.entity.PrescriptionItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PrescriptionItemDto {
    private Long id;
    private Long prescriptionId;
    private Long medicineId;
    private String medicineName;
    private String medicineType;
    private Integer prescribedQuantity;
    private Integer remainingQuantity;
    private boolean isCompleted;

    public static PrescriptionItemDto of(PrescriptionItem item) {
        PrescriptionItemDto dto = new PrescriptionItemDto();
        dto.setId(item.getId());
        dto.setPrescriptionId(item.getPrescription().getId());
        dto.setMedicineId(item.getMedicine().getId());
        dto.setMedicineName(item.getMedicine().getMedicineName());
        dto.setMedicineType(item.getMedicine().getMedicineType().getDisplayName());
        dto.setPrescribedQuantity(item.getPrescribedQuantity());
        dto.setRemainingQuantity(item.getRemainingQuantity());
        dto.setCompleted(item.isCompleted());
        return dto;
    }
}