package com.ai.balancelab_be.domain.challenge.service;

import com.ai.balancelab_be.domain.challenge.dto.ChallengeDTO;
import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import com.ai.balancelab_be.domain.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    // 챌린지 등록
    public Challenge createChallenge(ChallengeDTO challengeDTO) {
        Challenge challenge = new Challenge();
        challenge.setMemberId(challengeDTO.getMemberId());
        challenge.setGoal(challengeDTO.getGoal());
        challenge.setPeriod(challengeDTO.getPeriod());
        challenge.setTargetWeight(challengeDTO.getTargetWeight());
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(challengeDTO.getEndDate() != null ? challengeDTO.getEndDate() : challenge.getStartDate().plusMonths(Long.parseLong(challengeDTO.getPeriod())));  // 기간을 이용해 종료일 계산
        challenge.setCompleted(false);
        challenge.setRegDate(LocalDate.now());

        return challengeRepository.save(challenge);
    }

    // 사용자의 모든 챌린지 조회
    public List<Challenge> getChallengesByUserId(Long userId) {
        return challengeRepository.findByMemberId(userId);
    }

    // 진행 중인 챌린지 확인
    public Challenge checkOngoingChallenge(Long userId) {
        return challengeRepository.findByMemberIdAndIsCompleted(userId, false).orElse(null);
    }

    // 챌린지 실패 처리 (기간이 지나지 않았어도 실패한 경우)
    public void failChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("챌린지를 찾을 수 없습니다."));
        challenge.setCompleted(true);  // 실패 처리
        challengeRepository.save(challenge);
    }
}