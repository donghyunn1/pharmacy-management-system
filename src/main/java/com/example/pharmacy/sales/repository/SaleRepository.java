package com.example.pharmacy.sales.repository;

import com.example.pharmacy.sales.entity.Sale;
import com.example.pharmacy.sales.entity.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // 특정 기간 내 판매 내역 조회
    List<Sale> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    // 특정 고객의 판매 내역 조회
    List<Sale> findByCustomerIdOrderBySaleDateDesc(Long customerId);

    // 특정 상태의 판매 내역 조회
    List<Sale> findByStatusOrderBySaleDateDesc(SaleStatus status);

    // 특정 직원이 처리한 판매 내역 조회
    List<Sale> findByMemberIdOrderBySaleDateDesc(Long memberId);

    // 특정 처방전에 관련된 판매 내역 조회
    List<Sale> findByPrescriptionIdOrderBySaleDateDesc(Long prescriptionId);

    // 특정 기간 내 완료된 판매 내역 조회
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate AND s.status = 'COMPLETED' ORDER BY s.saleDate DESC")
    List<Sale> findCompletedSalesBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
