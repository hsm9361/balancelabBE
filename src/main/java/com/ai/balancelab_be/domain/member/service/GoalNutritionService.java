package com.ai.balancelab_be.domain.member.service;

import com.ai.balancelab_be.domain.member.repository.GoalNutritionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
public class GoalNutritionService {

    @Autowired
    private GoalNutritionRepository goalNutritionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteGoalNutrition(Long goalNutritionId) {
        if (goalNutritionRepository.existsById(goalNutritionId)) {
            goalNutritionRepository.deleteById(goalNutritionId);
        }
    }
}
