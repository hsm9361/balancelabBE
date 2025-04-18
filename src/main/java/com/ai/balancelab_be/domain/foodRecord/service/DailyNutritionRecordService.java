package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.entity.DailyNutritionRecordEntity;
import com.ai.balancelab_be.domain.foodRecord.entity.FoodRecordEntity;
import com.ai.balancelab_be.domain.foodRecord.repository.DailyNutritionRecordRepository;
import com.ai.balancelab_be.domain.foodRecord.repository.FoodRecordRepository;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;


import java.time.LocalDate;
import java.util.HashMap;
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

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void scheduledNutritionCalculation() {
        LocalDate today = LocalDate.now();

        List<FoodRecordEntity> todayFoodRecords = foodRecordRepository.findByConsumedDate(today);

        Map<Long, List<FoodRecordEntity>> groupedByMember = todayFoodRecords.stream()
                .collect(Collectors.groupingBy(FoodRecordEntity::getMemberId));

        for (Map.Entry<Long, List<FoodRecordEntity>> entry : groupedByMember.entrySet()) {
            Long memberId = entry.getKey();
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("멤버 없음: " + memberId));

            List<FoodRecordEntity> foodRecords = entry.getValue();

            boolean exists = dailyNutritionRecordRepository
                    .existsByMemberEntity_IdAndConsumedDate(member.getId(), today);
            if (exists) continue;

            // 🔄 음식 이름 + 양 + 단위로 구성된 리스트 생성
            List<Map<String, Object>> foodList = foodRecords.stream()
                    .map(record -> {
                        Map<String, Object> map = new HashMap<>(); // HashMap 사용
                        map.put("name", record.getFoodName());
                        map.put("amount", record.getAmount());
                        map.put("unit", record.getUnit());
                        return map;
                    })
                    .toList();


            // AI 서버 요청
            String url = "http://localhost:8000/nutrition/calculate";
            Map<String, Object> requestBody = Map.of(
                    "foodList", foodList,
                    "date", today.toString()
            );

            Map<String, Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();

            if (response == null || response.isEmpty()) {
                throw new RuntimeException("영양 정보 조회 실패 for member: " + member.getId());
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");

            if (data == null) {
                throw new RuntimeException("응답에 data 필드가 없습니다 for member: " + member.getId());
            }

            // 👇 응답에서 JSON 파싱
            DailyNutritionRecordEntity record = new DailyNutritionRecordEntity();
            record.setMemberEntity(member);
            record.setConsumedDate(today);
            record.setCarbo(toDouble(data.get("탄수화물")));
            record.setProtein(toDouble(data.get("단백질")));
            record.setFat(toDouble(data.get("지방")));
            record.setSugar(toDouble(data.get("당분")));
            record.setSodium(toDouble(data.get("나트륨")));
            record.setFibrin(toDouble(data.get("식이섬유")));
            record.setWater(toDouble(data.get("수분")));
            record.setCalories(toDouble(data.get("칼로리")));

            dailyNutritionRecordRepository.save(record);
        }
    }

    // 응답이 Double 또는 String일 수 있어서 안전하게 변환
    private Double toDouble(Object obj) {
        if (obj == null) return 0.0;  // null 값 처리
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();  // Number 타입이면 그대로 Double로 변환
        }
        try {
            return Double.parseDouble(obj.toString());  // String 타입이면 숫자로 변환
        } catch (Exception e) {
            return 0.0;  // 변환 실패 시 기본값 0.0
        }
    }
}