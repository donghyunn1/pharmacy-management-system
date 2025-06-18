// 양동현. 2025.06.18
package com.example.pharmacy.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class CustomerFormDto {
    private Long id;

    @NotBlank(message = "고객명은 필수 입력값입니다.")
    private String name;

    private LocalDate birthdate;

    @Pattern(regexp = "^[0-9-]+$", message = "전화번호는 숫자와 하이픈만 입력 가능합니다.")
    private String phone;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    private String address;

    private Boolean isMember = false;
}