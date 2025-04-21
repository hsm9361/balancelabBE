package com.ai.balancelab_be.domain.member.repository;

import com.ai.balancelab_be.domain.member.entity.GoalNutritionEntity;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GoalNutritionRepository extends JpaRepository<GoalNutritionEntity, Long> {
    Optional<GoalNutritionEntity> findByMember(MemberEntity member);
}