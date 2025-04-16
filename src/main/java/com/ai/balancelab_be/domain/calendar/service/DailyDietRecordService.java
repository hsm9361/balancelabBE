package com.ai.balancelab_be.domain.calendar.service;

import com.ai.balancelab_be.domain.calendar.dto.DailyDietRecordDto;
import com.ai.balancelab_be.domain.calendar.entity.DailyDietRecord;
import com.ai.balancelab_be.domain.calendar.repository.DailyDietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyDietRecordService {

    private final DailyDietRecordRepository recordRepository;

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
                                                              LocalDate start,
                                                              LocalDate end) {
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
}
