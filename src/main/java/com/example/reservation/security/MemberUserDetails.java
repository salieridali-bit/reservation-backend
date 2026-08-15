package com.example.reservation.security;

import com.example.reservation.entity.Member;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberUserDetails extends User {

    private final Long memberId;
    private final String name;

    public MemberUserDetails(Member member) {
        super(member.getEmail(), member.getPassword(), List.of());
        this.memberId = member.getId();
        this.name = member.getName();
    }
}
