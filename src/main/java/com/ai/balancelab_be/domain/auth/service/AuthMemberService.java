package com.ai.balancelab_be.domain.auth.service;

import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import com.ai.balancelab_be.domain.healthPrediction.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member saveIfNotExists(String email) {
        return memberRepository.findByEmail(email).orElseGet(() -> {
            Member newMember = new Member();
            newMember.setEmail(email);
            return memberRepository.save(newMember);
        });
    }
}
