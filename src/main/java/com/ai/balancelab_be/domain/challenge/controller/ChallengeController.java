package com.ai.balancelab_be.domain.challenge.controller;

import com.ai.balancelab_be.domain.challenge.dto.ChallengeDTO;
import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import com.ai.balancelab_be.domain.challenge.service.ChallengeService;
import com.ai.balancelab_be.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    // 챌린지 등록
    @PostMapping("/create")
    public ResponseEntity<?> createChallenge(
            @RequestBody ChallengeDTO challengeDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        challengeDTO.setMemberId(memberId);
        Challenge checkChallenge = challengeService.checkOngoingChallenge(memberId);
        if(checkChallenge != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "이미 진행 중인 챌린지가 있습니다."));
        }
        Challenge challenge = challengeService.createChallenge(challengeDTO);
        return ResponseEntity.ok(challenge);
    }

    // 사용자의 모든 챌린지 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Challenge>> getChallenges(@PathVariable Long userId) {
        List<Challenge> challenges = challengeService.getChallengesByUserId(userId);
        return ResponseEntity.ok(challenges);
    }

    // 진행 중인 챌린지 조회
    @GetMapping("/user/ongoing")
    public ResponseEntity<Challenge> getOngoingChallenge(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("얍");
        Long memberId = userDetails.getMemberId();
        Challenge challenge = challengeService.checkOngoingChallenge(memberId);
        return challenge != null ? ResponseEntity.ok(challenge) : ResponseEntity.notFound().build();
    }

    // 챌린지 실패 처리
    @PutMapping("/fail/{challengeId}")
    public ResponseEntity<String> failChallenge(@PathVariable Long challengeId) {
        challengeService.failChallenge(challengeId);
        return ResponseEntity.ok("챌린지 실패 처리 완료.");
    }
}