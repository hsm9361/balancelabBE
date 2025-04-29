package com.ai.balancelab_be.domain.challenge.service;

import com.ai.balancelab_be.domain.challenge.dto.ChallengeDTO;
import com.ai.balancelab_be.domain.challenge.entity.Challenge;
import com.ai.balancelab_be.domain.challenge.repository.ChallengeRepository;
import com.ai.balancelab_be.domain.member.entity.GoalNutritionEntity;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.GoalNutritionRepository;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.ai.balancelab_be.domain.member.service.GoalNutritionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalNutritionRepository goalNutritionRepository;

    @Mock
    private GoalNutritionService goalNutritionService;

    @InjectMocks
    private ChallengeService challengeService;

    private ChallengeDTO challengeDTO;
    private MemberEntity member;
    private Challenge challenge;

    @BeforeEach
    void setUp() {
        challengeDTO = new ChallengeDTO();
        challengeDTO.setMemberId(1L);
        challengeDTO.setGoal("감소");
        challengeDTO.setPeriod("1");
        challengeDTO.setPeriodUnit("개월");
        challengeDTO.setStartWeight(80.0);
        challengeDTO.setTargetWeight(75.0);

        member = new MemberEntity();
        member.setId(1L);
        member.setWeight(80.0);

        challenge = new Challenge();
        challenge.setId(1L);
        challenge.setMemberId(1L);
        challenge.setGoal("감소");
        challenge.setStartWeight(80.0);
        challenge.setTargetWeight(75.0);
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(LocalDate.now().plusMonths(1));
        challenge.setCompleted(false);
        challenge.setStatus(Challenge.ChallengeStatus.ONGOING);
    }

    @Test
    void createChallenge_성공() {
        when(challengeRepository.findByMemberIdAndIsCompletedFalseAndStatus(
                eq(1L), eq(Challenge.ChallengeStatus.ONGOING)))
                .thenReturn(Optional.empty());
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(challengeRepository.saveAndFlush(any(Challenge.class))).thenReturn(challenge);

        Challenge result = challengeService.createChallenge(challengeDTO);

        assertNotNull(result);
        assertEquals(1L, result.getMemberId());
        assertEquals("감소", result.getGoal());
        assertEquals(75.0, result.getTargetWeight());
        assertFalse(result.isCompleted());
        assertEquals(Challenge.ChallengeStatus.ONGOING, result.getStatus());
        verify(challengeRepository).saveAndFlush(any(Challenge.class));
    }

    @Test
    void checkOngoingChallenge_종료일_지난_경우_완료() {
        challenge.setEndDate(LocalDate.now().minusDays(1));
        when(challengeRepository.findByMemberIdAndIsCompletedFalseAndStatus(
                eq(1L), eq(Challenge.ChallengeStatus.ONGOING)))
                .thenReturn(Optional.of(challenge));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(goalNutritionRepository.findByMember(member)).thenReturn(Optional.empty());
        when(challengeRepository.save(challenge)).thenReturn(challenge);

        Challenge result = challengeService.checkOngoingChallenge(1L);

        assertNull(result);
        assertTrue(challenge.isCompleted());
        assertEquals(Challenge.ChallengeStatus.COMPLETED, challenge.getStatus());
        verify(challengeRepository).save(challenge);
    }

    @Test
    void checkOngoingChallenge_목표_몸무게_달성시_완료() {
        member.setWeight(75.0); // 목표 몸무게 달성
        when(challengeRepository.findByMemberIdAndIsCompletedFalseAndStatus(
                eq(1L), eq(Challenge.ChallengeStatus.ONGOING)))
                .thenReturn(Optional.of(challenge));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(goalNutritionRepository.findByMember(member)).thenReturn(Optional.empty());
        when(challengeRepository.save(challenge)).thenReturn(challenge);

        Challenge result = challengeService.checkOngoingChallenge(1L);

        assertNull(result);
        assertTrue(challenge.isCompleted());
        assertEquals(Challenge.ChallengeStatus.COMPLETED, challenge.getStatus());
        verify(challengeRepository).save(challenge);
    }

    @Test
    void failChallenge_성공() {
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(goalNutritionRepository.findByMember(member)).thenReturn(Optional.empty());
        when(challengeRepository.save(challenge)).thenReturn(challenge);

        challengeService.failChallenge(1L);

        assertTrue(challenge.isCompleted());
        assertEquals(Challenge.ChallengeStatus.FAILED, challenge.getStatus());
        verify(challengeRepository).save(challenge);
        verify(goalNutritionRepository, never()).delete(any(GoalNutritionEntity.class));
    }

    @Test
    void failChallenge_이미_완료된_챌린지_예외() {
        challenge.setCompleted(true);
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            challengeService.failChallenge(1L);
        });

        assertEquals("이미 완료된 챌린지입니다.", exception.getMessage());
        verify(challengeRepository, never()).save(any(Challenge.class));
    }

    @Test
    void getChallengesByUserId_성공() {
        when(challengeRepository.findByMemberIdOrderByEndDateDesc(1L))
                .thenReturn(Collections.singletonList(challenge));

        var result = challengeService.getChallengesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(challenge, result.get(0));
        verify(challengeRepository).findByMemberIdOrderByEndDateDesc(1L);
    }
}