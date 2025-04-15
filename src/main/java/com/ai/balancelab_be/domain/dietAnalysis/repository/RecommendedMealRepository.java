package com.ai.balancelab_be.domain.dietAnalysis.repository;

import com.ai.balancelab_be.domain.dietAnalysis.entity.RecommendedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedMealRepository extends JpaRepository<RecommendedMeal, Long> {
    // groupId로 조회
    List<RecommendedMeal> findByGroupId(Long groupId);
}
