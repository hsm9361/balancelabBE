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
    public MemberEntity saveIfNotExists(String email, String sub, String username) {
        return memberRepository.findByEmail(email).orElseGet(() -> {
            MemberEntity newMemberEntity = new MemberEntity();
            newMemberEntity.setEmail(email);
            newMemberEntity.setSub(sub);
            newMemberEntity.setMembername(username);
            return memberRepository.save(newMemberEntity);
        });
    }

    public Long getMemberIdByEmail(String email) {
        // 이메일로 MemberEntity를 찾고 memberId를 반환하는 로직 구현
        // 예시:
        return memberRepository.findByEmail(email)
                .map(MemberEntity::getId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }
}
