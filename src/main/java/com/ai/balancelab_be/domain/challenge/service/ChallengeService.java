package com.ai.balancelab_be.domain.challenge.service;

import com.ai.balancelab_be.domain.challenge.dto.ChallengeDTO;
import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import com.ai.balancelab_be.domain.challenge.repository.ChallengeRepository;
import com.ai.balancelab_be.domain.member.entity.GoalNutritionEntity;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.GoalNutritionRepository;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final GoalNutritionRepository goalNutritionRepository;

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

        // Save the challenge first to ensure it exists regardless of nutrition API status
        Challenge savedChallenge = challengeRepository.saveAndFlush(challenge);

        try {
            fetchAndSaveNutritionData(
                    challengeDTO.getMemberId(),
                    challengeDTO.getTargetWeight(),
                    challengeDTO.getEndDate() != null ? challengeDTO.getEndDate() : LocalDate.now().plusMonths(Long.parseLong(challengeDTO.getPeriod()))
            );
        } catch (Exception e) {
            System.out.println("Failed to fetch nutrition data for member ID " + challengeDTO.getMemberId() + ": " + e.getMessage());
        }

        return savedChallenge;
    }

    @Transactional
    private void fetchAndSaveNutritionData(Long memberId, Integer targetWeight, LocalDate endDate) {
        try {
            // JSON 페이로드 준비
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", memberId);
            requestBody.put("target_weight", targetWeight);
            requestBody.put("end_date", endDate.toString()); // LocalDate를 문자열로 변환

            String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

            // HttpClient 생성
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // POST 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/diet/goal-nutrition"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            // 요청 전송 및 응답 처리
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 확인
            if (response.statusCode() == 200) {
                // JSON 응답 파싱
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                Map<String, Object> nutritionData = (Map<String, Object>) responseBody.get("data");

                // 값 추출
                Double tdee = toDouble(nutritionData.get("tdee"));
                Double calories = toDouble(nutritionData.get("calories"));
                Double carb = toDouble(nutritionData.get("carb"));
                Double protein = toDouble(nutritionData.get("protein"));
                Double fat = toDouble(nutritionData.get("fat"));

                MemberEntity member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

                // 영양소 레코드 생성 및 저장
                GoalNutritionEntity record = new GoalNutritionEntity();
                record.setMember(member);
                record.setTdeeCalories(tdee);
                record.setGoalCalories(calories);
                record.setGoalCarbo(carb);
                record.setGoalProtein(protein);
                record.setGoalFat(fat);

                goalNutritionRepository.save(record);
            } else {
                throw new ServiceException("Failed to fetch nutrition data: " + response.statusCode());
            }
        } catch (ConnectException e) {
            throw new ServiceException("Could not connect to nutrition API service. Is it running?", e);
        } catch (Exception e) {
            throw new ServiceException("Error communicating with nutrition API: " + e.getMessage(), e);
        }
    }

    private Double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null; // 또는 기본값 처리
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
        Long memberId = challenge.getMemberId();
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));
        Optional<GoalNutritionEntity> goalNutritionOpt = goalNutritionRepository.findByMember(member);
        goalNutritionOpt.ifPresent(goalNutrition -> goalNutritionRepository.delete(goalNutrition));
    }
}