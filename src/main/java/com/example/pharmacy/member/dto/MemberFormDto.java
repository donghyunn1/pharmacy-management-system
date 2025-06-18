// 양동현. 2025.06.18
package com.example.pharmacy.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberFormDto {

    @NotBlank(message = "사용자명은 필수 입력값입니다.")
    private String username;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    private String phone;

    private LocalDate birthdate;
}
