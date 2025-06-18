// 양동현. 2025.06.18
package com.example.pharmacy.customer.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter @Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private LocalDate birthdate;

    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_member")
    private Boolean isMember = false;

    // 나이 계산 메서드
    public Integer getAge() {
        if (birthdate == null) {
            return null;
        }
        return LocalDate.now().getYear() - birthdate.getYear();
    }

    // 연락처 마스킹 메서드 (개인정보 보호)
    public String getMaskedPhone() {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return phone.substring(0, phone.length() - 4) + "****";
    }
}
