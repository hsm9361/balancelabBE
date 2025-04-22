package com.ai.balancelab_be.domain.challenge.repository;

import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 특정 사용자의 모든 챌린지 목록을 가져오기
    List<Challenge> findByMemberId(Long memberId);

    // 특정 사용자의 진행 중인 챌린지 찾기
    Optional<Challenge> findByMemberIdAndIsCompleted(Long memberId, boolean isCompleted);
}
