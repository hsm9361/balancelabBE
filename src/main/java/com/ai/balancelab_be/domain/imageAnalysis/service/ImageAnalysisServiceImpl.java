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
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ImageAnalysisServiceImpl implements ImageAnalysisService {

    private final RestTemplate restTemplate;
    private final FoodAnalysisRepository foodAnalysisRepository;


    @Override
    public Map<String, Object> analyzeDiet(Long userId, String filePath) {
        String url = "http://localhost:8000/analyze/image";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("file_path", filePath);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();

            if (body == null) {
                throw new RuntimeException("분석 결과가 없습니다.");
            }

            return body;

        } catch (HttpClientErrorException.BadRequest e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new RuntimeException("분석 실패: 서버로부터 응답이 없습니다.");
            }

            try {
                ErrorResponse error = new ObjectMapper().readValue(responseBody, ErrorResponse.class);
                throw new RuntimeException("분석 실패: " + error.getDetail());
            } catch (Exception ex) {
                throw new RuntimeException("분석 실패: 응답을 JSON으로 파싱하지 못했습니다. 원시 응답: " + responseBody);
            }
        }
    }
}