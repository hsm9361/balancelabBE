package com.ai.balancelab_be.domain.imageAnalysis.repository;

import com.ai.balancelab_be.domain.imageAnalysis.entity.FoodAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodAnalysisRepository extends JpaRepository<FoodAnalysisEntity, Long> {
}