// 양동현. 2025.06.18
package com.example.pharmacy.prescription.repository;

import com.example.pharmacy.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 유효한 처방전 조회
    @Query("SELECT p FROM Prescription p WHERE p.expiryDate >= CURRENT_DATE AND p.status = 'active'")
    List<Prescription> findValidPrescriptions();

}