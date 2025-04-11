package com.ai.balancelab_be.domain.dietAnalysis.service;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietAnalysisService {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    public DietAnalysisService(){
        this.restTemplate = new RestTemplate();
    }

    public DietAnalysisResponse getFoodNameResponse(DietAnalysisRequest request) {
        try {
            String url = fastApiUrl + "/analysis/foodName";
            DietAnalysisResponse response = restTemplate.postForObject(url, request, DietAnalysisResponse.class);
            if (response != null) {
                System.out.println("서비스단체크: " + response.getFoodList());
                System.out.println("서비스단체크 (nutrition): " + response.getNutrition());
                System.out.println("서비스단체크 (deficientNutrients): " + response.getDeficientNutrients());
                System.out.println("서비스단체크 (nextMealSuggestion): " + response.getNextMealSuggestion());
                return response;
            } else {
                System.out.println("FastAPI로부터 응답을 받았으나 null입니다.");
                return new DietAnalysisResponse(List.of(), new Nutrition(0, 0, 0, 0, 0, 0, 0), List.of(), List.of());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze diet with FastAPI: " + e.getMessage());
        }
    }
}
//-------------------------------------------------------------------------------------------
//package com.ai.balancelab_be.domain.dietAnalysis.service;
//
//import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//
//public class DietAnalysisService {
//
//    @Value("${fastapi.url:http://localhost:8000}")
//    private String fastApiUrl;
//
//    private final RestTemplate restTemplate;
//
//    public DietAnalysisService(){
//        this.restTemplate = new RestTemplate();
//    }
//
//    public List<String> getFoodNameResponse(DietAnalysisRequest request) {
//        try {
//            // FastAPI로 요청 준비
//            String url = fastApiUrl + "/analysis/foodName";
//            FoodNameResponse response = restTemplate.postForObject(url, request, FoodNameResponse.class);
//            if (response != null && response.getFoodList() != null) {
//                System.out.println("서비스단체크: " + response.getFoodList());
//                return response.getFoodList();
//            } else {
//                System.out.println("FastAPI로부터 응답을 받았으나 foodList가 null입니다. 응답 객체: " + response);
//                return null;
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to analyze diet with FastAPI: " + e.getMessage());
//        }
//    }
//
////    public void analyzeDiet(String userId) {
////        // 1. 사용자 식단 데이터 조회
////        // 2. 식단 데이터 분석
////        // 3. 분석 결과 저장
////    }
//}
//
//class FoodNameResponse {
//    private List<String> foodList;
//
//    // JSON의 "food_list" 키를 이 필드에 매핑하도록 명시
//    @JsonProperty("food_list")
//    public List<String> getFoodList() {
//        return foodList;
//    }
//
//    @JsonProperty("food_list")
//    public void setFoodList(List<String> foodList) {
//        this.foodList = foodList;
//    }
//}
