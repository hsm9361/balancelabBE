package com.ai.balancelab_be.domain.foodRecord.service;

import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordDto;
import com.ai.balancelab_be.domain.foodRecord.entity.FoodRecordEntity;
import com.ai.balancelab_be.domain.foodRecord.repository.FoodRecordRepository;
import com.ai.balancelab_be.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodRecordServiceImpl implements FoodRecordService {

    private final FoodRecordRepository foodRecordRepository;

    @Transactional
    @Override
    public List<FoodRecordDto> createFoodRecord(List<FoodRecordDto> foodRecordDto) {
        List<FoodRecordEntity> entities = foodRecordDto.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        List<FoodRecordEntity> saved = foodRecordRepository.saveAll(entities);
        return saved.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public FoodRecordDto findById(Long id) {
        FoodRecordEntity entity = foodRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food record not found with id: " + id));
        return mapToDto(entity);
    }

    @Override
    public List<FoodRecordDto> findByMemberId(Long memberId) {
        List<FoodRecordEntity> entities = foodRecordRepository.findByMemberId(memberId);
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<FoodRecordDto> findByMemberIdAndGroupId(Long memberId, String groupId) {
        List<FoodRecordEntity> entities = foodRecordRepository.findByMemberIdAndGroupId(memberId, groupId);
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public FoodRecordDto updateFoodRecord(Long id, FoodRecordDto foodRecordDto) {
        FoodRecordEntity entity = foodRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food record not found with id: " + id));

        updateEntity(entity, foodRecordDto);
        FoodRecordEntity updated = foodRecordRepository.save(entity);
        return mapToDto(updated);
    }

    @Transactional
    @Override
    public void deleteFoodRecord(Long id) {
        if (!foodRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food record not found with id: " + id);
        }
        foodRecordRepository.deleteById(id);
    }

    @Override
    public List<FoodRecordDto> findByMemberIdAndConsumedDate(Long memberId, LocalDateTime consumedDate) {
        List<FoodRecordEntity> entities = foodRecordRepository.findByMemberIdAndConsumedDate(memberId, consumedDate);
        return entities.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private FoodRecordEntity mapToEntity(FoodRecordDto dto) {
        return FoodRecordEntity.builder()
                .id(dto.getId())
                .foodName(dto.getFoodName())
                .groupId(dto.getGroupId())
                .memberId(dto.getMemberId())
                .carbohydrates(dto.getCarbohydrates() != null ? dto.getCarbohydrates() : 0.0)
                .fat(dto.getFat() != null ? dto.getFat() : 0.0)
                .fiber(dto.getFiber() != null ? dto.getFiber() : 0.0)
                .protein(dto.getProtein() != null ? dto.getProtein() : 0.0)
                .sodium(dto.getSodium() != null ? dto.getSodium() : 0.0)
                .sugar(dto.getSugar() != null ? dto.getSugar() : 0.0)
                .water(dto.getWater() != null ? dto.getWater() : 0.0)
                .type(dto.getType())
                .unit(dto.getUnit())
                .amount(dto.getAmount())
                .mealTime(dto.getMealTime())
                .regDate(dto.getRegDate())
                .uptDate(dto.getUptDate())
                .consumedDate(dto.getConsumedDate())
                .build();
    }

    private FoodRecordDto mapToDto(FoodRecordEntity entity) {
        return FoodRecordDto.builder()
                .id(entity.getId())
                .foodName(entity.getFoodName())
                .groupId(entity.getGroupId())
                .memberId(entity.getMemberId())
                .carbohydrates(entity.getCarbohydrates())
                .fat(entity.getFat())
                .fiber(entity.getFiber())
                .protein(entity.getProtein())
                .sodium(entity.getSodium())
                .sugar(entity.getSugar())
                .water(entity.getWater())
                .type(entity.getType())
                .unit(entity.getUnit())
                .amount(entity.getAmount())
                .mealTime(entity.getMealTime())
                .regDate(entity.getRegDate())
                .uptDate(entity.getUptDate())
                .build();
    }

    private void updateEntity(FoodRecordEntity entity, FoodRecordDto dto) {
        entity.setFoodName(dto.getFoodName());
        entity.setGroupId(dto.getGroupId());
        entity.setMemberId(dto.getMemberId());
        entity.setCarbohydrates(dto.getCarbohydrates() != null ? dto.getCarbohydrates() : entity.getCarbohydrates());
        entity.setFat(dto.getFat() != null ? dto.getFat() : entity.getFat());
        entity.setFiber(dto.getFiber() != null ? dto.getFiber() : entity.getFiber());
        entity.setProtein(dto.getProtein() != null ? dto.getProtein() : entity.getProtein());
        entity.setSodium(dto.getSodium() != null ? dto.getSodium() : entity.getSodium());
        entity.setSugar(dto.getSugar() != null ? dto.getSugar() : entity.getSugar());
        entity.setWater(dto.getWater() != null ? dto.getWater() : entity.getWater());
        entity.setType(dto.getType());
        entity.setUnit(dto.getUnit());
        entity.setAmount(dto.getAmount());
        entity.setMealTime(dto.getMealTime());
    }
}