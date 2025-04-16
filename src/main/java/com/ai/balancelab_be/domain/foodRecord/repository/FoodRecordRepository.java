package com.ai.balancelab_be.domain.foodRecord.repository;

import com.ai.balancelab_be.domain.foodRecord.entity.FoodRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRecordRepository extends JpaRepository<FoodRecordEntity, Long> {
    List<FoodRecordEntity> findByMemberId(Long memberId);
    List<FoodRecordEntity> findByMemberIdAndGroupId(Long memberId, String groupId);
}