package com.ai.balancelab_be.domain.healthPrediction.service;

import com.ai.balancelab_be.domain.healthPrediction.dto.memberDTO;
import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import com.ai.balancelab_be.domain.healthPrediction.entity.DailyRecord;
import com.ai.balancelab_be.domain.healthPrediction.repository.MemberRepository;
import com.ai.balancelab_be.domain.healthPrediction.repository.DailyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final DailyRecordRepository dailyRecordRepository;

    public memberDTO getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // 기본 정보 설정
        memberDTO dto = new memberDTO();
        dto.setAge(member.getAge());
        dto.setHeight(member.getHeight());
        dto.setWeight(member.getWeight());
        dto.setGender(member.getGender());

        // 최근 7일간의 기록 조회
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<DailyRecord> records = dailyRecordRepository.findRecordsByMemberAndDateRange(
                memberId, startDate, endDate);

        // 평균값 계산
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
            dto.setCarbo_perday(Math.round((totalCarbo / recordCount) * 100.0) / 100.0);
            dto.setSugar_perday(Math.round((totalSugar / recordCount) * 100.0) / 100.0);
            dto.setFat_perday(Math.round((totalFat / recordCount) * 100.0) / 100.0);
            dto.setSodium_perday(Math.round((totalSodium / recordCount) * 100.0) / 100.0);
            dto.setFibrin_perday(Math.round((totalFibrin / recordCount) * 100.0) / 100.0);
            dto.setWater_perday(Math.round((totalWater / recordCount) * 100.0) / 100.0);
        } else {
            // 기록이 없는 경우 0으로 설정
            dto.setCarbo_perday(0.0);
            dto.setSugar_perday(0.0);
            dto.setFat_perday(0.0);
            dto.setSodium_perday(0.0);
            dto.setFibrin_perday(0.0);
            dto.setWater_perday(0.0);
        }

        return dto;
    }
} 