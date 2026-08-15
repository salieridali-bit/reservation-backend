package com.example.reservation.service;

import com.example.reservation.dto.MemberForm;
import com.example.reservation.entity.Member;
import com.example.reservation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(MemberForm form) {
        Member member = Member.builder()
                .name(form.getName())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .build();

        memberRepository.save(member);
    }
    
    public Member getMember(String email) {
    	return memberRepository.findByEmail(email)
    			.orElseThrow(() ->
    					new IllegalArgumentException("회원을 찾을 수 없습니다.")
    			);
    }
}
