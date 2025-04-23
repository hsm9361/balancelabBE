package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.dto.DailyNutritionRecordDto;
import com.ai.balancelab_be.domain.foodRecord.dto.NutritionSumDto;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void scheduledNutritionCalculation() { // 30초마다 15개씩 검사해서 ai로 영양소 분석

        List<FoodRecordEntity> unAnalyzedRecords = foodRecordRepository.findTop15ByAnalyzedIsFalseOrderByConsumedDateAsc();
        ObjectMapper objectMapper = new ObjectMapper();

        for (FoodRecordEntity record : unAnalyzedRecords) {
            try {
                Long memberId = record.getMemberId();
                LocalDateTime date = record.getConsumedDate();
                String foodName = record.getFoodName();
                double amount = record.getAmount();
                String unit = record.getUnit();

                // AI 요청
                String url = "http://localhost:8000/nutrition/calculate";
                Map<String, Object> foodItem = Map.of(
                        "name", foodName,
                        "amount", amount,
                        "unit", unit
                );

                Map<String, Object> requestBody = Map.of(
                        "foodList", List.of(foodItem)
                );

                Map<String, Object> response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody),
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                ).getBody();

                if (response == null || response.isEmpty()) {
                    throw new RuntimeException("영양 정보 조회 실패 for member: " + memberId + ", food: " + foodName);
                }

                Object dataRaw = response.get("data");

                if (dataRaw == null) {
                    throw new RuntimeException("응답에 data 필드가 없습니다 for member: " + memberId + ", food: " + foodName);
                }

                Map<String, Object> data;
                if (dataRaw instanceof String) {
                    data = objectMapper.readValue((String) dataRaw, new TypeReference<>() {
                    });
                } else if (dataRaw instanceof Map) {
                    data = (Map<String, Object>) dataRaw;
                } else {
                    throw new RuntimeException("예상치 못한 data 타입: " + dataRaw.getClass().getName());
                }

                if (isValidNutritionData(data)) {
                    record.setCalories(toDouble(data.get("칼로리")));
                    record.setCarbohydrates(toDouble(data.get("탄수화물")));
                    record.setProtein(toDouble(data.get("단백질")));
                    record.setFat(toDouble(data.get("지방")));
                    record.setSugar(toDouble(data.get("당분")));
                    record.setSodium(toDouble(data.get("나트륨")));
                    record.setFiber(toDouble(data.get("식이섬유")));
                    record.setWater(toDouble(data.get("수분")));

                    record.setAnalyzed(true);  // ✅ 성공시에만 true로 설정
                    foodRecordRepository.save(record);
                } else {
                    System.err.println("🚫 분석 실패 또는 불완전한 데이터: " + record.getId());
                }


            } catch (Exception e) {
                System.err.println("영양 정보 계산 실패: " + record.getId() + ", 에러: " + e.getMessage());
            }
        }
    }

    private boolean isValidNutritionData(Map<String, Object> data) {
        return data != null &&
                data.containsKey("칼로리") &&
                data.containsKey("탄수화물") &&
                data.containsKey("단백질") &&
                data.containsKey("지방");
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

    public List<DailyNutritionRecordDto> findRecordsByMemberIdAndDateRange(Long memberId, String dateRange) {
        LocalDate end = LocalDate.now();
        LocalDate start;

        switch (dateRange.toLowerCase()) {
            case "7days":
                start = end.minusDays(6); // 오늘 포함 7일
                break;
            case "30days":
                start = end.minusDays(29);
                break;
            default:
                throw new IllegalArgumentException("Invalid date range: " + dateRange);
        }

        List<DailyNutritionRecordEntity> entities = dailyNutritionRecordRepository.findByMemberEntity_IdAndConsumedDateBetween(
                memberId, start, end);

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private DailyNutritionRecordDto toDto(DailyNutritionRecordEntity entity) {
        return DailyNutritionRecordDto.builder()
                .id(entity.getId())
                .memberId(entity.getMemberEntity().getId())
                .calories(entity.getCalories())
                .protein(entity.getProtein())
                .carbo(entity.getCarbo())
                .fat(entity.getFat())
                .consumedDate(entity.getConsumedDate())
                .build();
    }

    public List<NutritionSumDto> getWeeklyNutritionSum(Long memberId) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(6).toLocalDate().atStartOfDay(); // 최근 7일 포함
        return foodRecordRepository.getWeeklyNutritionSum(memberId, startDate);
    }


}