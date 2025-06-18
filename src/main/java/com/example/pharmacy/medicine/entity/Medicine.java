// 양동현. 2025.06.18
package com.example.pharmacy.medicine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "medicines")
@Getter @Setter
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long id;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "manufacturer_name", nullable = false)
    private String manufacturerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "medicine_type", nullable = false)
    private MedicineType medicineType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String effects;

    @Column(name = "prescription_required")
    private Boolean prescriptionRequired = false;

    @Column(name = "minimum_stock")
    private Integer minimumStock = 10;
}
