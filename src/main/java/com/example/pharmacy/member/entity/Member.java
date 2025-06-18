// 양동현. 2025.06.18
package com.example.pharmacy.member.entity;

import com.example.pharmacy.member.constant.MemberRole;
import com.example.pharmacy.member.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Entity
@Getter @Setter
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    // Role을 기반으로 Spring Security 역할 반환
    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setUsername(memberFormDto.getUsername());
        member.setName(memberFormDto.getName());
        member.setPhone(memberFormDto.getPhone());

        // 비밀번호 암호화
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);

        // 기본 역할은 USER로 설정
        member.setRole(MemberRole.ADMIN);

        return member;
    }}
