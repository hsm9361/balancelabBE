package com.ai.balancelab_be.domain.healthPrediction.service;

import com.ai.balancelab_be.domain.healthPrediction.dto.HealthPredictionRequest;
import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import com.ai.balancelab_be.domain.healthPrediction.entity.DailyRecord;
import com.ai.balancelab_be.domain.healthPrediction.repository.MemberRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthPredictionService {

    private final MemberRepository memberRepository;
    private final DailyRecordRepository dailyRecordRepository;
    private final RestTemplate restTemplate;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public String predictHealth(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // 최근 7일간의 기록 조회
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<DailyRecord> records = dailyRecordRepository.findRecordsByMemberAndDateRange(
                memberId, startDate, endDate);

        // FastAPI 요청 객체 생성
        HealthPredictionRequest request = new HealthPredictionRequest();
        request.setAge(member.getAge());
        request.setGender(member.getGender());

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
}
