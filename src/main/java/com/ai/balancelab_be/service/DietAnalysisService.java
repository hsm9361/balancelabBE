package com.ai.balancelab_be.service;

import com.ai.balancelab_be.dto.FoodNameRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DietAnalysisService {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    public DietAnalysisService() {
        this.restTemplate = new RestTemplate();
    }

    public List<String> getFoodNameResponse(FoodNameRequest foodNameRequest){
        String url = fastApiUrl + "/analysis/foodName";
        FoodNameResponse response = restTemplate.postForObject(url, foodNameRequest, FoodNameResponse.class);

        if (response != null && response.getFoodList() != null) {
            return response.getFoodList();
        } else {
            System.out.println("FastAPI로부터 응답을 받았으나 foodList가 null입니다. 응답 객체: " + response);
            return null;
        }
    }
}

class FoodNameResponse {
    private List<String> foodList;

    // JSON의 "food_list" 키를 이 필드에 매핑하도록 명시
    @JsonProperty("food_list")
    public List<String> getFoodList() {
        return foodList;
    }

    @JsonProperty("food_list")
    public void setFoodList(List<String> foodList) {
        this.foodList = foodList;
    }
}
