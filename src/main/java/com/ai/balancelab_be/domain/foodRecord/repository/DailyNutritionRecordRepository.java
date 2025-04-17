package com.ai.balancelab_be.domain.foodRecord.repository;

import com.ai.balancelab_be.domain.foodRecord.entity.DailyNutritionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyNutritionRecordRepository extends JpaRepository<DailyNutritionRecordEntity, Long> {
    List<DailyNutritionRecordEntity> findByMemberEntity_IdAndRegDateBetween(
            Long member_id,
            LocalDateTime start,
            LocalDateTime end);
    boolean existsByMemberEntity_IdAndConsumedDate(Long memberId, LocalDate recordDate);

}