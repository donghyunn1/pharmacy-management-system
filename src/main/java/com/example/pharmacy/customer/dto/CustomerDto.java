// 양동현. 2025.06.18
package com.example.pharmacy.customer.dto;

import com.example.pharmacy.customer.entity.Customer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CustomerDto {
    private Long id;
    private String name;
    private LocalDate birthdate;
    private String phone;
    private String email;
    private String address;
    private Boolean isMember;
    private Integer age;
    private String maskedPhone;

    public static CustomerDto of(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setBirthdate(customer.getBirthdate());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setIsMember(customer.getIsMember());
        dto.setAge(customer.getAge());
        dto.setMaskedPhone(customer.getMaskedPhone());
        return dto;
    }
}
