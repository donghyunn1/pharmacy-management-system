// 양동현. 2025.06.18
package com.example.pharmacy.sales.repository;

import com.example.pharmacy.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    // 특정 고객의 판매 내역 조회
    List<Sale> findByCustomerIdOrderBySaleDateDesc(Long customerId);
}
