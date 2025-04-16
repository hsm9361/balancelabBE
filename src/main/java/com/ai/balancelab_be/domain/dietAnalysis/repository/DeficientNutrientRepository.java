package com.ai.balancelab_be.domain.dietAnalysis.repository;

import com.ai.balancelab_be.domain.dietAnalysis.entity.DeficientNutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeficientNutrientRepository extends JpaRepository<DeficientNutrient, Long> {
    // groupId로 조회
    Optional<DeficientNutrient> findByGroupId(Long groupId);
}
