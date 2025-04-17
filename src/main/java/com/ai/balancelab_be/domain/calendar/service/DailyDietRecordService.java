package com.ai.balancelab_be.domain.calendar.service;

import com.ai.balancelab_be.domain.calendar.dto.DailyDietRecordDto;
import com.ai.balancelab_be.domain.calendar.entity.DailyDietRecord;
import com.ai.balancelab_be.domain.calendar.repository.DailyDietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyDietRecordService {

    private final DailyDietRecordRepository recordRepository;
    public boolean deleteRecord(int foodId, int userId) {
        return recordRepository.findByFoodIdAndUserId(foodId, userId)
                .map(record -> {
                    recordRepository.delete(record);
                    return true;
                })
                .orElse(false);
    }


    public void saveDietRecords(int userId, List<DailyDietRecordDto> dtos) {
        List<DailyDietRecord> entities = dtos.stream()
                .map(dto -> DailyDietRecord.builder()
                        .userId(userId)
                        .foodName(dto.getFoodName())
                        .category(dto.getCategory())
                        .intakeAmount(dto.getIntakeAmount())
                        .unit(dto.getUnit())
                        .eatenDate(dto.getEatenDate())
                        .build())
                .collect(Collectors.toList());

        recordRepository.saveAll(entities);
    }

    public List<DailyDietRecordDto> getDietRecordsByDateRange(int userId,
                                                              java.time.LocalDateTime start,
                                                              java.time.LocalDateTime end) {
        return recordRepository.findByUserIdAndEatenDateBetween(userId, start, end).stream()
                .map(record -> DailyDietRecordDto.builder()
                        .foodName(record.getFoodName())
                        .category(record.getCategory())
                        .intakeAmount(record.getIntakeAmount())
                        .unit(record.getUnit())
                        .eatenDate(record.getEatenDate())
                        .build())
                .collect(Collectors.toList());
    }
    // DailyDietRecordService.java
    public List<DailyDietRecord> getRecords(int userId, LocalDateTime start, LocalDateTime end) {
        return recordRepository.findByUserIdAndEatenDateBetween(userId, start, end);
    }
}
