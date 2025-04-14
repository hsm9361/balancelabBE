package com.ai.balancelab_be.domain.calendar.repository;

import com.ai.balancelab_be.domain.calendar.entity.DailyDietRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface DailyDietRecordRepository extends JpaRepository<DailyDietRecord, Integer> {
    List<DailyDietRecord> findByUserIdAndEatenDateBetween(int userId,
                                                          java.time.LocalDateTime start,
                                                          java.time.LocalDateTime end);
    Optional<DailyDietRecord> findByFoodIdAndUserId(int foodId, int userId);
}
