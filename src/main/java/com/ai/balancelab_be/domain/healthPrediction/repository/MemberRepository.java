package com.ai.balancelab_be.domain.healthPrediction.repository;

import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 기본적인 CRUD 메서드는 JpaRepository에서 상속받아 사용 가능
    // 추가적인 커스텀 쿼리가 필요하다면 여기에 정의
} 