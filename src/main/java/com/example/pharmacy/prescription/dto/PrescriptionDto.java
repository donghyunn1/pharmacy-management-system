package com.example.pharmacy.prescription.dto;

import com.example.pharmacy.prescription.entity.Prescription;
import com.example.pharmacy.prescription.entity.PrescriptionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PrescriptionDto {
    private Long id;
    private Long customerId;
    private String customerName;
    private String doctorName;
    private String hospitalName;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private PrescriptionStatus status;
    private String statusDisplay;
    private boolean isValid;
    private boolean isExpired;
    private List<PrescriptionItemDto> prescriptionItems = new ArrayList<>();

    public static PrescriptionDto of(Prescription prescription) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setId(prescription.getId());
        dto.setCustomerId(prescription.getCustomer().getId());
        dto.setCustomerName(prescription.getCustomer().getName());
        dto.setDoctorName(prescription.getDoctorName());
        dto.setHospitalName(prescription.getHospitalName());
        dto.setIssueDate(prescription.getIssueDate());
        dto.setExpiryDate(prescription.getExpiryDate());
        dto.setStatus(prescription.getStatus());
        dto.setStatusDisplay(prescription.getStatus().getDisplayName());
        dto.setValid(prescription.isValid());
        dto.setExpired(prescription.isExpired());

        dto.setPrescriptionItems(prescription.getPrescriptionItems().stream()
                .map(PrescriptionItemDto::of)
                .collect(Collectors.toList()));

        return dto;
    }
}