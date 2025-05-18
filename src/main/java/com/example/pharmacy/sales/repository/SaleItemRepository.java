package com.example.pharmacy.sales.repository;

import com.example.pharmacy.sales.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    // 특정 판매의 모든 항목 조회
    List<SaleItem> findBySaleId(Long saleId);

    // 특정 약품이 포함된 판매 항목 조회
    List<SaleItem> findByMedicineId(Long medicineId);

    // 특정 재고로부터 판매된 항목 조회
    List<SaleItem> findByInventoryId(Long inventoryId);

    // 특정 처방전 항목과 연결된 판매 항목 조회
    List<SaleItem> findByPrescriptionItemId(Long prescriptionItemId);

    // 특정 약품의 판매 수량 합계 조회
    @Query("SELECT SUM(si.quantity) FROM SaleItem si WHERE si.medicine.id = :medicineId AND si.sale.status = 'COMPLETED'")
    Integer getTotalSoldQuantityByMedicineId(@Param("medicineId") Long medicineId);
}
