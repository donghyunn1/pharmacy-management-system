package com.example.pharmacy.prescription.repository;

import com.example.pharmacy.prescription.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    // 처방전별 항목 조회
    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);

    // 약품별 처방 항목 조회
    List<PrescriptionItem> findByMedicineId(Long medicineId);

    // 남은 수량이 있는 처방 항목 조회
    List<PrescriptionItem> findByPrescriptionIdAndRemainingQuantityGreaterThan(Long prescriptionId, Integer minQuantity);

    // 특정 처방전의 특정 약품 항목 조회
    @Query("SELECT pi FROM PrescriptionItem pi WHERE pi.prescription.id = :prescriptionId AND pi.medicine.id = :medicineId")
    PrescriptionItem findByPrescriptionIdAndMedicineId(@Param("prescriptionId") Long prescriptionId, @Param("medicineId") Long medicineId);
}