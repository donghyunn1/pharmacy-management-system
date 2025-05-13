package com.example.pharmacy.inventory.repository;

import com.example.pharmacy.inventory.entity.MedicineInventory;
import com.example.pharmacy.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<MedicineInventory, Long> {

    List<MedicineInventory> findByMedicineOrderByPurchaseDateAsc(Medicine medicine);

    List<MedicineInventory> findByMedicineAndStockQuantityGreaterThanOrderByPurchaseDateAsc(Medicine medicine, Integer minQuantity);

    @Query("SELECT mi FROM MedicineInventory mi WHERE mi.medicine.id = :medicineId AND mi.stockQuantity > 0 ORDER BY mi.purchaseDate ASC")
    List<MedicineInventory> findAvailableInventoryByMedicineIdOrderByPurchaseDate(@Param("medicineId") Long medicineId);

    @Query("SELECT mi FROM MedicineInventory mi WHERE mi.expirationDate <= :date AND mi.stockQuantity > 0")
    List<MedicineInventory> findExpiringItems(@Param("date") LocalDate date);

    @Query("SELECT SUM(mi.stockQuantity) FROM MedicineInventory mi WHERE mi.medicine.id = :medicineId AND mi.stockQuantity > 0 AND mi.expirationDate > CURRENT_DATE")
    Integer getTotalValidStockByMedicineId(@Param("medicineId") Long medicineId);

    List<MedicineInventory> findByMedicineAndExpirationDateBetweenAndStockQuantityGreaterThan(Medicine medicine, LocalDate startDate, LocalDate endDate, Integer minQuantity);

    List<MedicineInventory> findBySupplierNameContainingIgnoreCase(String supplierName);
}