package com.ai.balancelab_be.domain.calendar.controller;

import com.ai.balancelab_be.domain.calendar.dto.DailyDietRecordDto;
import com.ai.balancelab_be.domain.calendar.entity.DailyDietRecord;
import com.ai.balancelab_be.domain.calendar.service.DailyDietRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diet")
@CrossOrigin(origins = "http://localhost:3000")  // ✅ 프론트 주소 허용
@RequiredArgsConstructor
public class DailyDietRecordController {

    private final DailyDietRecordService recordService;

    @DeleteMapping("/delete/{foodId}")
    public ResponseEntity<String> deleteDiet(
            @PathVariable int foodId,
            @RequestParam int userId
    ) {
        boolean deleted = recordService.deleteRecord(foodId, userId);
        if (deleted) {
            return ResponseEntity.ok("삭제되었습니다.");
        } else {
            return ResponseEntity.status(404).body("삭제할 데이터를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/save")
    public String saveDietRecords(@RequestParam int userId, @RequestBody List<DailyDietRecordDto> records) {
        recordService.saveDietRecords(userId, records);
        return "success";
    }

    @GetMapping("/list")
    public ResponseEntity<List<DailyDietRecordDto>> getList(
            @RequestParam int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<DailyDietRecord> records = recordService.getRecords(userId, startDate, endDate); // ✅ 여기를 바꿔야 함

        List<DailyDietRecordDto> result = records.stream()
                .map(r -> new DailyDietRecordDto(
                        r.getFoodId(),
                        r.getFoodName(),
                        r.getCategory(),
                        r.getIntakeAmount(),
                        r.getUnit(),
                        r.getEatenDate()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}
