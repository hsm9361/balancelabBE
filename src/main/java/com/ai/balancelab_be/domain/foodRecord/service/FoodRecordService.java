package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordDto;

import java.time.LocalDate;
import java.util.List;

public interface FoodRecordService {
    List<FoodRecordDto> createFoodRecord(List<FoodRecordDto> foodRecordDto);
    FoodRecordDto findById(Long id);
    List<FoodRecordDto> findByMemberId(Long memberId);
    List<FoodRecordDto> findByMemberIdAndGroupId(Long memberId, String groupId);
    FoodRecordDto updateFoodRecord(Long id, FoodRecordDto foodRecordDto);
    List<FoodRecordDto> findByMemberIdAndConsumedDateBetween(Long id, LocalDate start, LocalDate end);
    void deleteFoodRecord(Long id);
}
