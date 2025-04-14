package com.ai.balancelab_be.domain.member.repository;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 이메일을 기준으로 회원을 찾는 메서드 정의
    Optional<MemberEntity> findByEmail(String email);
}
