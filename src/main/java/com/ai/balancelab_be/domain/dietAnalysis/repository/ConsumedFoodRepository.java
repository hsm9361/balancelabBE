package com.ai.balancelab_be.domain.dietAnalysis.repository;

import com.ai.balancelab_be.domain.dietAnalysis.entity.ConsumedFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsumedFoodRepository extends JpaRepository<ConsumedFood, Long> {
    // 동일 groupId로 조회
    List<ConsumedFood> findByGroupId(Long groupId);

    // 사용자별 조회
    List<ConsumedFood> findByEmail(String email);

    // 사용자와 날짜로 조회
    List<ConsumedFood> findByEmailAndCreatedDate(String email, LocalDate createdDate);
}
