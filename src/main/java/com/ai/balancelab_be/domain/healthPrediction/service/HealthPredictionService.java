package com.ai.balancelab_be.domain.healthPrediction.service;

import com.ai.balancelab_be.domain.healthPrediction.dto.HealthPredictionRequest;
import com.ai.balancelab_be.domain.healthPrediction.dto.PredictionSaveDto;
import com.ai.balancelab_be.domain.healthPrediction.entity.PredictRecord;
import com.ai.balancelab_be.domain.healthPrediction.repository.PredictRecordRepository;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.foodRecord.entity.DailyNutritionRecordEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.ai.balancelab_be.domain.foodRecord.repository.DailyNutritionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthPredictionService {

    private final MemberRepository memberRepository;
    private final DailyNutritionRecordRepository dailyNutritionRecordRepository;
    private final RestTemplate restTemplate;
    private final PredictRecordRepository predictRecordRepository;

    @Value("${fastapi.url}")
    private String fastApiUrl;


    public ResponseEntity<String> predictHealth(HealthPredictionRequest dto) {
        Long memberId = dto.getMemberId();
        // 최근 7일간의 기록 조회
        LocalDate today = LocalDate.from(LocalDate.now().atStartOfDay());

        LocalDate endDate = today.minusDays(1); //어제
        LocalDate startDate = endDate.minusDays(6);

        Optional<MemberEntity> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 사용자 ID입니다.");
        }
        MemberEntity member = memberOptional.get();


        List<DailyNutritionRecordEntity> records = dailyNutritionRecordRepository.findByMemberEntity_IdAndConsumedDateBetween(
                memberId, startDate, endDate);

        // FastAPI 요청 객체 생성
        HealthPredictionRequest request = new HealthPredictionRequest();

        request.setMemberId(memberId);
        request.setAge(member.getAge());
        request.setGender(Objects.equals(member.getGender(), "MALE") ?0:1);
        request.setHeight(member.getHeight());
        request.setWeight(member.getWeight());
        request.setSmokeDaily(dto.getSmokeDaily());
        request.setDrinkWeekly(dto.getDrinkWeekly());
        request.setExerciseWeekly(dto.getExerciseWeekly());
        request.setHistoryDiabetes(dto.getHistoryDiabetes());
        request.setHistoryHypertension(dto.getHistoryHypertension());
        request.setHistoryCardiovascular(dto.getHistoryCardiovascular());

        // 영양소 값 계산
        if (!records.isEmpty()) {
            double totalCarbo = 0;
            double totalSugar = 0;
            double totalFat = 0;
            double totalSodium = 0;
            double totalFibrin = 0;
            double totalWater = 0;

            for (DailyNutritionRecordEntity record : records) {
                totalCarbo += record.getCarbo();
                totalSugar += record.getSugar();
                totalFat += record.getFat();
                totalSodium += record.getSodium();
                totalFibrin += record.getFibrin();
                totalWater += record.getWater();
            }

            int recordCount = records.size();
            request.setDailyCarbohydrate(Math.round((totalCarbo / recordCount) * 100.0) / 100.0);
            request.setDailySugar(Math.round((totalSugar / recordCount) * 100.0) / 100.0);
            request.setDailyFat(Math.round((totalFat / recordCount) * 100.0) / 100.0);
            request.setDailySodium(Math.round((totalSodium / recordCount) * 100.0) / 100.0);
            request.setDailyFibrin(Math.round((totalFibrin / recordCount) * 100.0) / 100.0);
            request.setDailyWater(Math.round((totalWater / recordCount) * 100.0) / 100.0);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("등록된 식단이 없습니다!");
        }

        // FastAPI 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // FastAPI 요청 전송
        HttpEntity<HealthPredictionRequest> entity = new HttpEntity<>(request, headers);
        System.out.println(entity);
        try {
            String response = restTemplate.postForObject(
                    fastApiUrl + "/predict/health",
                    entity,
                    String.class
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FastAPI 요청 실패: " + e.getMessage());
        }
    }

    @Transactional
    public String savePredictionRecord(PredictionSaveDto predictDto) {
        try {
            // 회원 정보 조회
            String email = predictDto.getEmail();
            MemberEntity member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));

            // 예측 기록 생성
            PredictRecord record = PredictRecord.builder()
                    .memberEntity(member)
                    .dailyCarbohydrate(predictDto.getDailyCarbohydrate())
                    .dailySugar(predictDto.getDailySugar())
                    .dailyFat(predictDto.getDailyFat())
                    .dailySodium(predictDto.getDailySodium())
                    .dailyFibrin(predictDto.getDailyFibrin())
                    .dailyWater(predictDto.getDailyWater())
                    .historyDiabetes(predictDto.getHistoryDiabetes())
                    .historyHypertension(predictDto.getHistoryHypertension())
                    .historyCvd(predictDto.getHistoryCvd())
                    .diabetesProba(predictDto.getDiabetesProba())
                    .hypertensionProba(predictDto.getHypertensionProba())
                    .cvdProba(predictDto.getCvdProba())
                    .smokeDaily(predictDto.getSmokeDaily())
                    .drinkWeekly(predictDto.getDrinkWeekly())
                    .exerciseWeekly(predictDto.getExerciseWeekly())
                    .build();

            // 예측 기록 저장
            predictRecordRepository.save(record);

            // 성공 메시지 반환
            return "저장 성공";
        } catch (Exception e) {
            // 예외 발생 시 실패 메시지 반환
            return "저장 실패: " + e.getMessage();
        }
    }
}
