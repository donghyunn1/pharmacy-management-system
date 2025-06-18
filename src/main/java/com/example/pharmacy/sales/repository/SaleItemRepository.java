// 양동현. 2025.06.18
package com.example.pharmacy.sales.repository;

import com.example.pharmacy.sales.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    // 특정 약품의 판매 수량 합계 조회
    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.medicine.id = :medicineId AND si.sale.status = 'COMPLETED'")
    Integer getTotalSoldQuantityByMedicineId(@Param("medicineId") Long medicineId);
}
