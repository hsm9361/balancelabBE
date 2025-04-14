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

    @Query("SELECT d FROM DailyRecord d WHERE d.memberEntity.id = :memberId AND d.recordDate BETWEEN :start AND :end")
    List<DailyRecord> findByMemberEntity_IdAndRecordDateBetween(
            @Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}