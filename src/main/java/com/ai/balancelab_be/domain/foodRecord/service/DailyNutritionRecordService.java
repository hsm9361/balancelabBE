package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.entity.DailyNutritionRecordEntity;
import com.ai.balancelab_be.domain.foodRecord.entity.FoodRecordEntity;
import com.ai.balancelab_be.domain.foodRecord.repository.DailyNutritionRecordRepository;
import com.ai.balancelab_be.domain.foodRecord.repository.FoodRecordRepository;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyNutritionRecordService {

    private final DailyNutritionRecordRepository dailyNutritionRecordRepository;
    private final MemberRepository memberRepository;
    private final FoodRecordRepository foodRecordRepository; // 음식 기록 접근용
    private final RestTemplate restTemplate = new RestTemplate(); // 또는 @Bean으로 등록

    @Scheduled(cron = "0 22 11 * * *") // 매일 오후 11시 59분
    @Transactional
    public void scheduledNutritionCalculation() {
        LocalDate today = LocalDate.now();

        // 1. 오늘 먹은 음식 기록만 가져오기
        List<FoodRecordEntity> todayFoodRecords = foodRecordRepository.findByConsumedDate(today);

        // 2. 멤버별로 그룹핑
        Map<Long, List<FoodRecordEntity>> groupedByMember = todayFoodRecords.stream()
                .collect(Collectors.groupingBy(FoodRecordEntity::getMemberId));

        for (Map.Entry<Long, List<FoodRecordEntity>> entry : groupedByMember.entrySet()) {
            Long memberId = entry.getKey();
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("멤버 없음: " + memberId));

            List<FoodRecordEntity> foodRecords = entry.getValue();

            // 3. 이미 해당 멤버의 기록이 있다면 skip (중복 저장 방지)

            boolean exists = dailyNutritionRecordRepository
                    .existsByMemberEntity_IdAndConsumedDate(member.getId(), today);
            if (exists) continue;

            // 4. 음식 이름만 추출
            List<String> foodNames = foodRecords.stream()
                    .map(FoodRecordEntity::getFoodName)
                    .collect(Collectors.toList());

            // 5. AI 서버 호출
            String url = "http://localhost:8000/nutrition/calculate";
            Map<String, Object> requestBody = Map.of(
                    "foodNames", foodNames,
                    "date", today.toString()
            );

            Map<String, Double> response = restTemplate.postForObject(url, requestBody, Map.class);

            if (response == null) {
                throw new RuntimeException("영양 정보 조회 실패 for member: " + member.getId());
            }

            // 6. DailyNutritionRecord 생성 및 저장
            DailyNutritionRecordEntity record = new DailyNutritionRecordEntity();
            record.setMemberEntity(member);
            record.setConsumedDate(today); // 새로 추가한 필드
            record.setCalories(response.getOrDefault("calories", 0.0));
            record.setProtein(response.getOrDefault("protein", 0.0));
            record.setCarbo(response.getOrDefault("carbo", 0.0));
            record.setSugar(response.getOrDefault("sugar", 0.0));
            record.setFat(response.getOrDefault("fat", 0.0));
            record.setSodium(response.getOrDefault("sodium", 0.0));
            record.setFibrin(response.getOrDefault("fibrin", 0.0));
            record.setWater(response.getOrDefault("water", 0.0));

            dailyNutritionRecordRepository.save(record);
        }
    }


}