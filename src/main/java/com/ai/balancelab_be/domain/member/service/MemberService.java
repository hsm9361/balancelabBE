package com.ai.balancelab_be.domain.member.service;

import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 이메일을 통해 회원 정보 조회
    public MemberEntity getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberInfo(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));

        MemberDto dto = new MemberDto();
        dto.setAge(memberEntity.getAge());
        dto.setHeight(memberEntity.getHeight());
        dto.setWeight(memberEntity.getWeight());
        dto.setGender(memberEntity.getGender());

        return dto;
    }

}
