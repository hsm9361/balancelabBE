package com.ai.balancelab_be.domain.foodRecord.controller;

import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordCountDto;
import com.ai.balancelab_be.domain.foodRecord.dto.FoodRecordDto;
import com.ai.balancelab_be.domain.foodRecord.service.FoodRecordService;
import com.ai.balancelab_be.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/food-record")
public class FoodRecordController {

    private final FoodRecordService foodRecordService;

    // 새로운 식단 기록을 생성하는 엔드포인트
    // 사용자가 인증된 경우에만 호출 가능하며, FoodRecordDto를 받아 DB에 저장
    @PostMapping("/create")
    public ResponseEntity<List<FoodRecordDto>> createFoodRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody List<FoodRecordDto> foodRecordDto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FoodRecordDto> dtoWithMemberId = foodRecordDto.stream()
                .peek(dto -> dto.setMemberId(userDetails.getMemberId()))
                .collect(Collectors.toList());

        List<FoodRecordDto> created = foodRecordService.createFoodRecord(dtoWithMemberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 특정 ID로 식단 기록을 조회하는 엔드포인트
    // 인증된 사용자가 자신의 식단 기록만 조회할 수 있도록 제한
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodRecordDto> getFoodRecordById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FoodRecordDto foodRecord = foodRecordService.findById(id);
        if (!foodRecord.getMemberId().equals(userDetails.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(foodRecord);
    }

    // 특정 사용자의 모든 식단 기록을 조회하는 엔드포인트
    // 인증된 사용자의 memberId를 기반으로 해당 사용자의 기록만 반환
    @GetMapping("/member")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FoodRecordDto>> getFoodRecordsByMember(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FoodRecordDto> records = foodRecordService.findByMemberId(userDetails.getMemberId());
        return ResponseEntity.ok(records);
    }

    // 특정 그룹 ID로 식단 기록을 조회하는 엔드포인트
    // 인증된 사용자가 자신의 그룹 ID에 해당하는 식단 기록만 조회 가능
    @GetMapping("/group/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FoodRecordDto>> getFoodRecordsByGroupId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String groupId) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FoodRecordDto> records = foodRecordService.findByMemberIdAndGroupId(userDetails.getMemberId(), groupId);
        return ResponseEntity.ok(records);
    }

    // 회원 ID와 날짜로 식단 기록을 조회하는 엔드포인트
    @GetMapping("/member/date")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FoodRecordDto>> getFoodRecordsByMemberAndDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") String date) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            LocalDate parsedDate = LocalDate.parse(date); // YYYY-MM-DD
            // LocalDate를 LocalDateTime으로 변환 (하루 시작: 00:00:00)
            LocalDateTime startOfDay = parsedDate.atStartOfDay();
            List<FoodRecordDto> records = foodRecordService.findByMemberIdAndConsumedDate(
                    userDetails.getMemberId(), startOfDay);
            return ResponseEntity.ok(records);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 특정 회원의 월간 식단 기록을 가져오는 엔드포인트
    @GetMapping("/member/range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FoodRecordCountDto>> getFoodRecordsByMemberAndMonth(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr) {
        System.out.println("확인");
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            LocalDateTime startDate = LocalDate.parse(startDateStr).atStartOfDay();
            LocalDateTime endDate = LocalDate.parse(endDateStr).atTime(23, 59, 59);
            List<FoodRecordCountDto> records = foodRecordService.getFoodRecordCounts(
                    userDetails.getMemberId(), startDate, endDate);
            return ResponseEntity.ok(records);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }



    // 기존 식단 기록을 업데이트하는 엔드포인트
    // 인증된 사용자가 자신의 식단 기록만 수정할 수 있도록 제한
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FoodRecordDto> updateFoodRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody FoodRecordDto foodRecordDto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FoodRecordDto existing = foodRecordService.findById(id);
        if (!existing.getMemberId().equals(userDetails.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        foodRecordDto.setMemberId(userDetails.getMemberId());
        FoodRecordDto updated = foodRecordService.updateFoodRecord(id, foodRecordDto);
        return ResponseEntity.ok(updated);
    }

    // 특정 식단 기록을 삭제하는 엔드포인트
    // 인증된 사용자가 자신의 식단 기록만 삭제할 수 있도록 제한
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFoodRecord(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FoodRecordDto existing = foodRecordService.findById(id);
        if (!existing.getMemberId().equals(userDetails.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        foodRecordService.deleteFoodRecord(id);
        return ResponseEntity.noContent().build();
    }
}