package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordCountDto;
import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodRecordService {
    List<FoodRecordDto> createFoodRecord(List<FoodRecordDto> foodRecordDto);
    FoodRecordDto findById(Long id);
    List<FoodRecordDto> findByMemberId(Long memberId);
    List<FoodRecordDto> findByMemberIdAndGroupId(Long memberId, String groupId);
    FoodRecordDto updateFoodRecord(Long id, FoodRecordDto foodRecordDto);
    void deleteFoodRecord(Long id);
    List<FoodRecordDto> findByMemberIdAndConsumedDate(Long memberId, LocalDateTime consumedDate);
    List<FoodRecordCountDto> getFoodRecordCounts(Long memberId, LocalDateTime startDate, LocalDateTime endDate);

}
