package com.ai.balancelab_be.domain.member.service;

import com.ai.balancelab_be.domain.member.dto.WeightHistoryDTO;
import com.ai.balancelab_be.domain.member.entity.WeightHistoryEntity;
import com.ai.balancelab_be.domain.member.repository.WeightHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeightHistoryService {
    private final WeightHistoryRepository weightHistoryRepository;

    @Transactional(readOnly = true)
    public List<WeightHistoryDTO> getWeightHistory(Long memberId, LocalDateTime startDate) {
        List<WeightHistoryEntity> entities = weightHistoryRepository
                .findByMemberIdAndInsDateAfterOrderByInsDateAsc(memberId, startDate);
        return entities.stream()
                .map(e -> new WeightHistoryDTO(e.getId(), e.getWeight(), e.getInsDate()))
                .collect(Collectors.toList());
    }
}