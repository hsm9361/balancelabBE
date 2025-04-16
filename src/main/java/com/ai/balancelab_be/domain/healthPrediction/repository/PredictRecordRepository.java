package com.ai.balancelab_be.domain.healthPrediction.repository;

import com.ai.balancelab_be.domain.healthPrediction.entity.PredictRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictRecordRepository extends JpaRepository<PredictRecord, Long> {
}
