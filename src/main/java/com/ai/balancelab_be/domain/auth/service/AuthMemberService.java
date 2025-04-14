package com.ai.balancelab_be.domain.auth.service;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberEntity saveIfNotExists(String email) {
        return memberRepository.findByEmail(email).orElseGet(() -> {
            MemberEntity newMemberEntity = new MemberEntity();
            newMemberEntity.setEmail(email);
            return memberRepository.save(newMemberEntity);
        });
    }
}
