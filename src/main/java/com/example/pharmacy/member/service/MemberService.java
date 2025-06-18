// 양동현. 2025.06.18
package com.example.pharmacy.member.service;

import com.example.pharmacy.member.dto.MemberFormDto;
import com.example.pharmacy.member.entity.Member;
import com.example.pharmacy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        validateDuplicateMember(memberFormDto.getUsername());
        return memberRepository.save(Member.createMember(memberFormDto, passwordEncoder));
    }

    private void validateDuplicateMember(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username);

        if (member == null) {
            throw new UsernameNotFoundException(username);
        }

        // Spring Security에서 사용할 User 객체 생성
        // Member의 role(ADMIN/USER)에 따라 권한 부여
        return User.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
