package com.ai.balancelab_be.domain.challenge.service;

import com.ai.balancelab_be.domain.challenge.dto.ChallengeDTO;
import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import com.ai.balancelab_be.domain.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    // 챌린지 등록
    @Transactional
    public Challenge createChallenge(ChallengeDTO challengeDTO) {
        // 진행 중인 챌린지 확인
        Challenge existingChallenge = checkOngoingChallenge(challengeDTO.getMemberId());
        if (existingChallenge != null) {
            throw new IllegalStateException("이미 진행 중인 챌린지가 있습니다.");
        }

        Challenge challenge = new Challenge();
        challenge.setMemberId(challengeDTO.getMemberId());
        challenge.setGoal(challengeDTO.getGoal());
        challenge.setPeriod(challengeDTO.getPeriod());
        challenge.setPeriodUnit(challengeDTO.getPeriodUnit());
        challenge.setStartWeight(challengeDTO.getStartWeight());
        challenge.setTargetWeight(challengeDTO.getTargetWeight());
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(
                challengeDTO.getEndDate() != null
                        ? challengeDTO.getEndDate()
                        : LocalDate.now().plusMonths(Long.parseLong(challengeDTO.getPeriod()))
        );
        challenge.setCompleted(false);
        challenge.setStatus(Challenge.ChallengeStatus.ONGOING);
        challenge.setRegDate(LocalDate.now());

        return challengeRepository.save(challenge);
    }

    // 사용자의 모든 챌린지 조회
    @Transactional(readOnly = true)
    public List<Challenge> getChallengesByUserId(Long userId) {
        return challengeRepository.findByMemberIdOrderByEndDateDesc(userId);
    }

    // 진행 중인 챌린지 확인
    @Transactional
    public Challenge checkOngoingChallenge(Long userId) {
        Optional<Challenge> challengeOpt = challengeRepository.findByMemberIdAndIsCompletedFalseAndStatus(
                userId, Challenge.ChallengeStatus.ONGOING);

        if (challengeOpt.isPresent()) {
            Challenge challenge = challengeOpt.get();
            LocalDate currentDate = LocalDate.now();
            LocalDate endDate = challenge.getEndDate();

            // endDate가 지난 경우 완료 처리
            if (currentDate.isAfter(endDate)) {
                challenge.setCompleted(true);
                challenge.setStatus(Challenge.ChallengeStatus.COMPLETED);
                challengeRepository.save(challenge);
                return null; // 완료된 챌린지는 진행 중으로 간주하지 않음
            }
            return challenge;
        }
        return null;
    }

    // 챌린지 실패 처리
    @Transactional
    public void failChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("챌린지를 찾을 수 없습니다."));

        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료된 챌린지입니다.");
        }

        challenge.setCompleted(true);
        challenge.setStatus(Challenge.ChallengeStatus.FAILED);
        challengeRepository.save(challenge);
    }
}