package com.ai.balancelab_be.domain.healthPrediction.repository;

import com.ai.balancelab_be.domain.healthPrediction.entity.DailyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    List<DailyRecord> findByMemberEntity_MemberIdAndRegDateBetween(
            Long member_id,
            LocalDateTime start,
            LocalDateTime end);

}