package com.ai.balancelab_be.domain.calendar.controller;

import com.ai.balancelab_be.domain.calendar.dto.DailyDietRecordDto;
import com.ai.balancelab_be.domain.calendar.service.DailyDietRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return recordService.getDietRecordsByDateRange(userId, startDate, endDate);
    }
}
