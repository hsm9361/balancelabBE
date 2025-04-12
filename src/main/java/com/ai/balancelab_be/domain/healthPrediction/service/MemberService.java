package com.ai.balancelab_be.domain.healthPrediction.service;

import com.ai.balancelab_be.domain.healthPrediction.dto.memberDTO;
import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import com.ai.balancelab_be.domain.healthPrediction.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 이메일을 통해 회원 정보 조회
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public memberDTO getMemberInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));

        memberDTO dto = new memberDTO();
        dto.setAge(member.getAge());
        dto.setHeight(member.getHeight());
        dto.setWeight(member.getWeight());
        dto.setGender(member.getGender());

        return dto;
    }

}
