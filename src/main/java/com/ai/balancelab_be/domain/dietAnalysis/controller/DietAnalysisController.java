package com.ai.balancelab_be.domain.dietAnalysis.controller;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
import com.ai.balancelab_be.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-analysis")
public class DietAnalysisController {

    private final DietAnalysisService dietAnalysisService;

    @PostMapping(value = "/message")
    public ResponseEntity<DietAnalysisResponse> DietAnalysis(
            @RequestParam("message") String message,
            @RequestParam(value = "mealTime", required = false) String mealTime,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            System.out.println("멤버아이디: " + userDetails.getMemberId());
            System.out.println("컨트롤러 메세지: " + message);
            System.out.println("컨트롤러 mealTime: " + mealTime);

            // message를 앞뒤 공백을 제거한 후 빈 문자열인지 혹은 null인지 확인
            if (message == null || message.trim().isEmpty()) {
                System.out.println("message가 비어 있음");
                return ResponseEntity.ok( // badRequest 대신 ok로 변경하여 정상 응답으로 처리
                        new DietAnalysisResponse(
                                Collections.emptyList(), // 빈 배열 반환
                                Collections.emptyList(),
                                new Nutrition(0, 0, 0, 0, 0, 0, 0),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                );
            }

            Long memberId = userDetails.getMemberId();
            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message, memberId, mealTime);
            DietAnalysisResponse response = dietAnalysisService.getDietAnalysisResponse(dietAnalysisRequest);
            System.out.println("컨트롤러 (foodList): " + response.getFoodList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("컨트롤러 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok( // 에러 발생 시에도 빈 배열 반환
                    new DietAnalysisResponse(
                            Collections.emptyList(), // 빈 배열 반환
                            Collections.emptyList(),
                            new Nutrition(0, 0, 0, 0, 0, 0, 0),
                            Collections.emptyList(),
                            Collections.emptyList()
                    )
            );
        }
    }
}