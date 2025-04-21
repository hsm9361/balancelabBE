package com.ai.balancelab_be.domain.member.repository;

import com.ai.balancelab_be.domain.member.entity.WeightHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.entity.GoalNutritionEntity;

import java.util.Optional;

public interface WeightHistoryRepository extends JpaRepository<WeightHistoryEntity, Long> {
    Optional<GoalNutritionEntity> findByMember(MemberEntity member);
}