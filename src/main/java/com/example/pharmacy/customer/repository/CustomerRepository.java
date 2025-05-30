package com.example.pharmacy.customer.repository;

import com.example.pharmacy.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // 이름으로 고객 검색 (부분 일치)
    List<Customer> findByNameContainingIgnoreCase(String name);

    // 전화번호로 고객 검색
    Customer findByPhone(String phone);

    // 이메일로 고객 검색
    Customer findByEmail(String email);

    // 회원 여부로 필터링
    List<Customer> findByIsMember(Boolean isMember);

    // 복합 검색 (이름 또는 전화번호 또는 이메일)
    @Query("SELECT c FROM Customer c WHERE " +
            "(:searchQuery IS NULL OR :searchQuery = '' OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
            "c.phone LIKE CONCAT('%', :searchQuery, '%') OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) AND " +
            "(:isMember IS NULL OR c.isMember = :isMember)")
    List<Customer> searchCustomers(@Param("searchQuery") String searchQuery,
                                   @Param("isMember") Boolean isMember);

    // 특정 필드로 검색
    @Query("SELECT c FROM Customer c WHERE " +
            "(:searchBy = 'name' AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) OR " +
            "(:searchBy = 'phone' AND c.phone LIKE CONCAT('%', :searchQuery, '%')) OR " +
            "(:searchBy = 'email' AND LOWER(c.email) LIKE LOWER(CONCAT('%', :searchQuery, '%')))")
    List<Customer> searchCustomersByField(@Param("searchBy") String searchBy,
                                          @Param("searchQuery") String searchQuery);

    // 전화번호 중복 체크
    boolean existsByPhone(String phone);

    // 이메일 중복 체크 (이메일이 있는 경우에만)
    boolean existsByEmailAndEmailIsNotNull(String email);
}
