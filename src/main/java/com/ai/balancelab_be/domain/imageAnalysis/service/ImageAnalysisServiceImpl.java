package com.ai.balancelab_be.domain.imageAnalysis.service;

import com.ai.balancelab_be.domain.imageAnalysis.dto.ErrorResponse;
import com.ai.balancelab_be.domain.imageAnalysis.dto.FoodAnalysisResponse;
import com.ai.balancelab_be.domain.imageAnalysis.entity.FoodAnalysisEntity;
import com.ai.balancelab_be.domain.imageAnalysis.repository.FoodAnalysisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ImageAnalysisServiceImpl implements ImageAnalysisService {

    private final RestTemplate restTemplate;
    private final FoodAnalysisRepository foodAnalysisRepository;


    @Override
    public String analyzeDiet(Long userId, String filePath) {
        String url = "http://localhost:8000/analyze/image";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_path", filePath);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
             ResponseEntity<FoodAnalysisResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    FoodAnalysisResponse[].class
            );

            FoodAnalysisResponse[] body = response.getBody();

            if (body == null || body.length == 0) {
                return "분석 결과가 없습니다.";
            }

            StringBuilder result = new StringBuilder("분석된 음식 정보:\n");
            for (FoodAnalysisResponse food : body) {
                result.append("- ")
                        .append(food.getFood_name())
                        .append(" (")
                        .append(food.getCalories())
                        .append(" kcal)\n");

                // DB 저장
                foodAnalysisRepository.save(
                        FoodAnalysisEntity.builder()
                                .userId(userId)
                                .foodName(food.getFood_name())
                                .calories(food.getCalories())
                                .carbohydrates(food.getNutrients().getCarbohydrates())
                                .fat(food.getNutrients().getFat())
                                .sugar(food.getNutrients().getSugar())
                                .sodium(food.getNutrients().getSodium())
                                .fiber(food.getNutrients().getFiber())
                                .water(food.getNutrients().getWater())
                                .build()
                );
            }

            return result.toString();

        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                return "분석 실패: 서버로부터 응답이 없습니다.";
            }

            try {
                ErrorResponse error = new ObjectMapper().readValue(responseBody, ErrorResponse.class);
                return "분석 실패: " + error.getDetail();
            } catch (Exception ex) {
                return "분석 실패: 응답을 JSON으로 파싱하지 못했습니다. 원시 응답: " + responseBody;
            }
        }
    }
}