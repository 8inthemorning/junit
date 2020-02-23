package com.example.junit.member;

import com.example.junit.domain.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findById(Long memberId);

    void validate(Long memberId);
}
