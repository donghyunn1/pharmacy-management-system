package com.example.pharmacy.prescription.repository;

import com.example.pharmacy.prescription.entity.Prescription;
import com.example.pharmacy.prescription.entity.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 고객별 처방전 조회
    List<Prescription> findByCustomerIdOrderByIssueDateDesc(Long customerId);

    // 상태별 처방전 조회
    List<Prescription> findByStatusOrderByIssueDateDesc(PrescriptionStatus status);

    // 만료 예정 처방전 조회
    List<Prescription> findByExpiryDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, PrescriptionStatus status);

    // 유효한 처방전 조회
    @Query("SELECT p FROM Prescription p WHERE p.expiryDate >= CURRENT_DATE AND p.status = 'active'")
    List<Prescription> findValidPrescriptions();

    // 특정 약품이 포함된 처방전 조회
    @Query("SELECT DISTINCT p FROM Prescription p JOIN p.prescriptionItems pi WHERE pi.medicine.id = :medicineId")
    List<Prescription> findByMedicineId(@Param("medicineId") Long medicineId);
}