package com.ai.balancelab_be.domain.foodRecord.repository;

import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordCountDto;
import com.ai.balancelab_be.domain.foodRecord.dto.NutritionSumDto;
import com.ai.balancelab_be.domain.foodRecord.entity.FoodRecordEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodRecordRepository extends JpaRepository<FoodRecordEntity, Long> {
    List<FoodRecordEntity> findByMemberId(Long memberId);
    List<FoodRecordEntity> findByMemberIdAndGroupId(Long memberId, String groupId);
    List<FoodRecordEntity> findByConsumedDate(LocalDate date);
    List<FoodRecordEntity> findByMemberIdAndConsumedDate(Long memberId, LocalDateTime consumedDate, Sort sort);
    List<FoodRecordEntity> findByMemberIdAndConsumedDateBetween(Long memberId, LocalDateTime startDate, LocalDateTime endDate);
    @Query("SELECT new com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordCountDto(COUNT(f.consumedDate), f.consumedDate) " +
            "FROM FoodRecordEntity f " +
            "WHERE f.memberId = :memberId " +
            "AND f.consumedDate BETWEEN :startDate AND :endDate " +
            "GROUP BY f.consumedDate")
    List<FoodRecordCountDto> findFoodRecordCountByDateRange(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    List<FoodRecordEntity> findTop15ByAnalyzedIsFalseOrderByConsumedDateAsc();

    @Query("""
        SELECT new com.ai.balancelab_be.domain.foodRecord.dto.NutritionSumDto(
            SUM(f.calories),
            SUM(f.carbohydrates),
            SUM(f.protein),
            SUM(f.fiber),
            SUM(f.sugar),
            SUM(f.sodium),
            SUM(f.fat),
            SUM(f.water),
            FUNCTION('DATE', f.consumedDate)
        )
        FROM FoodRecordEntity f
        WHERE f.memberId = :memberId
        AND f.consumedDate >= :startDate
        GROUP BY FUNCTION('DATE', f.consumedDate)
        ORDER BY FUNCTION('DATE', f.consumedDate) DESC
        """)
    List<NutritionSumDto> getWeeklyNutritionSum(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDateTime startDate
    );

}