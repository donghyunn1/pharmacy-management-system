// 양동현. 2025.06.18
package com.example.pharmacy.medicine.repository;

import com.example.pharmacy.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

}
