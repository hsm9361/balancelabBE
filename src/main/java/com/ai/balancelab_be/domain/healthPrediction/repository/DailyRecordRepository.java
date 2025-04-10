package com.ai.balancelab_be.domain.healthPrediction.repository;

import com.ai.balancelab_be.domain.healthPrediction.entity.DailyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRecordRepository extends JpaRepository<DailyRecord, Long> {
    
    @Query("SELECT dr FROM DailyRecord dr WHERE dr.member.id = :memberId AND dr.recordDate BETWEEN :startDate AND :endDate")
    List<DailyRecord> findRecordsByMemberAndDateRange(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
} 