package com.ai.balancelab_be.domain.dietAnalysis.controller;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-analysis")
public class DietAnalysisController {

    private final DietAnalysisService dietAnalysisService;

    @PostMapping(value = "/message")
    public ResponseEntity<DietAnalysisResponse> receiveDietMessage(
            @RequestParam("message") String message) {
        try {
            System.out.println("컨트롤러 메세지1(컨트롤러에 들어온값): " + message);
            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message);
            DietAnalysisResponse response = dietAnalysisService.getFoodNameResponse(dietAnalysisRequest);
            System.out.println("컨트롤러 메세지2(서비스에서 넘어온 전체값): " + response);
            System.out.println("컨트롤러 메세지2(foodList): " + response.getFoodList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    new DietAnalysisResponse(List.of(), new Nutrition(0, 0, 0, 0, 0, 0), List.of(), List.of())
            );
        }
    }
}

//-------------------------------------------------------------------------------------------
//package com.ai.balancelab_be.domain.dietAnalysis.controller;
//
//import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
//import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/diet-analysis")
//public class DietAnalysisController {
//
//    // 서비스는 나중에 추가할 예정이므로 주석 처리
//    // private final DietAnalysisService dietAnalysisService;
//
//    @Autowired
//    private DietAnalysisService dietAnalysisService;
//
//    @PostMapping(value = "/message")
//    public ResponseEntity<String> receiveDietMessage(
//            @RequestParam("message") String message) {
//        try {
//            // 프론트에서 넘어온 foodText와 userId 확인
//            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message);
//            List<String> food_list = dietAnalysisService.getFoodNameResponse(dietAnalysisRequest);
//            // 콘솔에 값 출력 (테스트용)
//            System.out.println("컨트롤러 메세지1(컨트롤러에 들어온값): " + message);
//            System.out.println("컨트롤러 메세지2(서비스에서 넘어온값): " + food_list);
//
////            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message);
////
////            List<String> food_list = dietAnalysisService.getFoodNameResponse(dietAnalysisRequest);
//
//            // 성공 응답 반환 (테스트용)
//            String responseMessage = "Diet message received successfully: " + message;
//            return ResponseEntity.ok(responseMessage);
//        } catch (Exception e) {
//            // 예외 발생 시 에러 응답
//            return ResponseEntity.badRequest().body("Failed to process diet message: " + e.getMessage());
//        }
//    }
//}