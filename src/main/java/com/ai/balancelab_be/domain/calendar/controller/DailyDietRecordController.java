package com.ai.balancelab_be.domain.calendar.controller;

import com.ai.balancelab_be.domain.calendar.dto.DailyDietRecordDto;
import com.ai.balancelab_be.domain.calendar.service.DailyDietRecordService;
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

    @PostMapping("/save")
    public String saveDietRecords(@RequestParam int userId, @RequestBody List<DailyDietRecordDto> records) {
        recordService.saveDietRecords(userId, records);
        return "success";
    }

    @GetMapping("/list")
    public List<DailyDietRecordDto> getDietRecords(
            @RequestParam int userId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return recordService.getDietRecordsByDateRange(userId, start, end);
    }
}
