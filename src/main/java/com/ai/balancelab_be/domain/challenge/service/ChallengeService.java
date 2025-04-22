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
import java.util.List;
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
        Challenge challenge = new Challenge();
        challenge.setMemberId(challengeDTO.getMemberId());
        challenge.setGoal(challengeDTO.getGoal());
        challenge.setPeriod(challengeDTO.getPeriod());
        challenge.setTargetWeight(challengeDTO.getTargetWeight());
        challenge.setStartDate(LocalDate.now());
        challenge.setEndDate(challengeDTO.getEndDate() != null ? challengeDTO.getEndDate() :
                challenge.getStartDate().plusMonths(Long.parseLong(challengeDTO.getPeriod())));
        challenge.setCompleted(false);
        challenge.setRegDate(LocalDate.now());

        // Save the challenge first to ensure it exists regardless of nutrition API status
        Challenge savedChallenge = challengeRepository.save(challenge);

        Long id = challengeDTO.getMemberId();

        // Separate the nutrition data fetching to improve error handling
        try {
            fetchAndSaveNutritionData(id);
        } catch (Exception e) {
            System.out.println("Failed to fetch nutrition data for member ID {}: {}"+ id+ e.getMessage()+ e);
            challengeRepository.save(challenge);
        }

        return savedChallenge;
    }

    @Transactional
    private void fetchAndSaveNutritionData(Long memberId) {
        try {
            // Prepare JSON payload
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(Map.of("id", memberId));

            // Create HttpClient with timeout
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // Build POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/diet/goal-nutrition"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            // Send request and get response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if response is successful
            if (response.statusCode() == 200) {
                // Parse JSON response
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);

                Map<String, Object> nutritionData = (Map<String, Object>) responseBody.get("data");

                // Extract values
                Double tdee = toDouble(nutritionData.get("tdee"));
                Double calories = toDouble(nutritionData.get("calories"));
                Double carb = toDouble(nutritionData.get("carb"));
                Double protein = toDouble(nutritionData.get("protein"));
                Double fat = toDouble(nutritionData.get("fat"));

                MemberEntity member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

                // Create or update nutrition record
                GoalNutritionEntity record = new GoalNutritionEntity();
                record.setMember(member);
                record.setTdeeCalories(tdee);
                record.setGoalCalories(calories);
                record.setGoalCarbo(carb);
                record.setGoalProtein(protein);
                record.setGoalFat(fat);

                // Save the nutrition entity
                goalNutritionRepository.save(record);
            } else {
                // Log response body for debugging
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