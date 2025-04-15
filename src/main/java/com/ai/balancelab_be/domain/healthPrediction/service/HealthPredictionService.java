package com.ai.balancelab_be.domain.healthPrediction.service;

import com.ai.balancelab_be.domain.healthPrediction.dto.HealthPredictionRequest;
import com.ai.balancelab_be.domain.healthPrediction.dto.HealthPredictionResponse;
import com.ai.balancelab_be.domain.healthPrediction.entity.PredictRecord;
import com.ai.balancelab_be.domain.healthPrediction.repository.PredictRecordRepository;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.healthPrediction.entity.DailyRecord;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.ai.balancelab_be.domain.healthPrediction.repository.DailyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthPredictionService {

    private final MemberRepository memberRepository;
    private final DailyRecordRepository dailyRecordRepository;
    private final RestTemplate restTemplate;
    private final PredictRecordRepository predictRecordRepository;

    @Value("${fastapi.url}")
    private String fastApiUrl;


    public String predictHealth(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // 최근 7일간의 기록 조회
        LocalDateTime endDate = LocalDate.now().atStartOfDay();
        LocalDateTime startDate = endDate.minusDays(7);
        
        List<DailyRecord> records = dailyRecordRepository.findByMemberEntity_MemberIdAndRegDateBetween(
                memberId, startDate, endDate);

        // FastAPI 요청 객체 생성
        HealthPredictionRequest request = new HealthPredictionRequest();
        request.setAge(memberEntity.getAge());
        request.setGender(memberEntity.getGender());

        // 영양소 값 계산
        if (!records.isEmpty()) {
            double totalCarbo = 0;
            double totalSugar = 0;
            double totalFat = 0;
            double totalSodium = 0;
            double totalFibrin = 0;
            double totalWater = 0;

            for (DailyRecord record : records) {
                totalCarbo += record.getCarbo();
                totalSugar += record.getSugar();
                totalFat += record.getFat();
                totalSodium += record.getSodium();
                totalFibrin += record.getFibrin();
                totalWater += record.getWater();
            }

            int recordCount = records.size();
            request.setCarbo(Math.round((totalCarbo / recordCount) * 100.0) / 100.0);
            request.setSugar(Math.round((totalSugar / recordCount) * 100.0) / 100.0);
            request.setFat(Math.round((totalFat / recordCount) * 100.0) / 100.0);
            request.setSodium(Math.round((totalSodium / recordCount) * 100.0) / 100.0);
            request.setFibrin(Math.round((totalFibrin / recordCount) * 100.0) / 100.0);
            request.setWater(Math.round((totalWater / recordCount) * 100.0) / 100.0);
        } else {
            // 기록이 없는 경우 0으로 설정
            request.setCarbo(0.0);
            request.setSugar(0.0);
            request.setFat(0.0);
            request.setSodium(0.0);
            request.setFibrin(0.0);
            request.setWater(0.0);
        }

        // FastAPI 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // FastAPI 요청 전송
        HttpEntity<HealthPredictionRequest> entity = new HttpEntity<>(request, headers);
        String response = restTemplate.postForObject(
                fastApiUrl + "/predict/health",
                entity,
                String.class
        );

        return response;
    }
    @Transactional
    public String savePredictionRecord(String email,
                                       double dailyCarbohydrate, double dailySugar, double dailyFat, double dailySodium, double dailyFibrin, double dailyWater,
                                       int historyDiabetes, int historyHypertension, int historyCvd,
                                       double diabetesProba, double hypertensionProba, double cvdProba,
                                       int smokeDaily, int drinkWeekly, int exerciseWeekly) {
        try {
            // 회원 정보 조회
            MemberEntity member = memberRepository.findByEmail(email)
                    .orElseGet(() -> {
                        throw new IllegalArgumentException("Member not found with email: " + email);
                    });

            // 예측 기록 생성
            PredictRecord record = new PredictRecord();
            record.setMemberEntity(member);
            record.setDailyCarbohydrate(dailyCarbohydrate);
            record.setDailySugar(dailySugar);
            record.setDailyFat(dailyFat);
            record.setDailySodium(dailySodium);
            record.setDailyFibrin(dailyFibrin);
            record.setDailyWater(dailyWater);
            record.setHistoryDiabetes(historyDiabetes);
            record.setHistoryHypertension(historyHypertension);
            record.setHistoryCvd(historyCvd);
            record.setDiabetesProba(diabetesProba);
            record.setHypertensionProba(hypertensionProba);
            record.setCvdProba(cvdProba);
            record.setSmokeDaily(smokeDaily);
            record.setDrinkWeekly(drinkWeekly);
            record.setExerciseWeekly(exerciseWeekly);


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
