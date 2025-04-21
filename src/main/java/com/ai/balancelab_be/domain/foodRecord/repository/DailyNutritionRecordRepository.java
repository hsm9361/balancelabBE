package com.ai.balancelab_be.domain.foodRecord.repository;

import com.ai.balancelab_be.domain.foodRecord.entity.DailyNutritionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyNutritionRecordRepository extends JpaRepository<DailyNutritionRecordEntity, Long> {
    List<DailyNutritionRecordEntity> findByMemberEntity_IdAndConsumedDateBetween(
            Long member_id,
            LocalDate start,
            LocalDate end);
    boolean existsByMemberEntity_IdAndConsumedDate(Long memberId, LocalDate recordDate);

}