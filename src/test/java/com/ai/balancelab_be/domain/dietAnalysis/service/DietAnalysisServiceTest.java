package com.ai.balancelab_be.domain.dietAnalysis.service;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.FoodNutrition;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.entity.ConsumedFood;
import com.ai.balancelab_be.domain.dietAnalysis.entity.DeficientNutrient;
import com.ai.balancelab_be.domain.dietAnalysis.entity.RecommendedMeal;
import com.ai.balancelab_be.domain.dietAnalysis.repository.ConsumedFoodRepository;
import com.ai.balancelab_be.domain.dietAnalysis.repository.DeficientNutrientRepository;
import com.ai.balancelab_be.domain.dietAnalysis.repository.RecommendedMealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DietAnalysisServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConsumedFoodRepository consumedFoodRepository;

    @Mock
    private DeficientNutrientRepository deficientNutrientRepository;

    @Mock
    private RecommendedMealRepository recommendedMealRepository;

    @InjectMocks
    private DietAnalysisService dietAnalysisService;

    private DietAnalysisRequest request;
    private DietAnalysisResponse response;

    @BeforeEach
    void setUp() {
        // FastAPI URL 설정
        ReflectionTestUtils.setField(dietAnalysisService, "fastApiUrl", "http://localhost:8000");

        // 요청 객체 초기화
        request = new DietAnalysisRequest();
        request.setMessage("사과, 바나나");
        request.setMemberId(1L);
        request.setMealTime("아침");

        // 응답 객체 초기화
        Nutrition nutrition = new Nutrition(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0);
        FoodNutrition foodNutrition = new FoodNutrition("사과", nutrition);
        response = new DietAnalysisResponse(
                Arrays.asList("사과", "바나나"),
                Arrays.asList(foodNutrition),
                new Nutrition(10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0),
                Arrays.asList("단백질", "식이섬유"),
                Arrays.asList("샐러드", "스무디")
        );
    }

    @Test
    void fetchFastApiResponse_성공() {
        // 준비
        when(restTemplate.postForObject(anyString(), any(), eq(DietAnalysisResponse.class)))
                .thenReturn(response);

        // 실행
        DietAnalysisResponse result = dietAnalysisService.fetchFastApiResponse(request);

        // 검증
        assertNotNull(result);
        assertEquals(2, result.getFoodList().size());
        assertEquals("사과", result.getFoodList().get(0));
        assertEquals(1, result.getNutritionPerFood().size());
        assertEquals(10.0, result.getNutritionPerFood().get(0).getNutrition().getProtein());
        assertEquals(2, result.getDeficientNutrients().size());
        assertEquals(2, result.getNextMealSuggestion().size());
        verify(restTemplate).postForObject(eq("http://localhost:8000/analysis/diet"), eq(request), eq(DietAnalysisResponse.class));
    }

    @Test
    void fetchFastApiResponse_빈_음식_리스트() {
        // 준비
        DietAnalysisResponse emptyResponse = new DietAnalysisResponse(
                Collections.emptyList(),
                Collections.emptyList(),
                new Nutrition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                Collections.emptyList(),
                Collections.emptyList()
        );
        when(restTemplate.postForObject(anyString(), any(), eq(DietAnalysisResponse.class)))
                .thenReturn(emptyResponse);

        // 실행
        DietAnalysisResponse result = dietAnalysisService.fetchFastApiResponse(request);

        // 검증
        assertNotNull(result);
        assertTrue(result.getFoodList().isEmpty());
        assertTrue(result.getNutritionPerFood().isEmpty());
        assertEquals(0.0, result.getTotalNutrition().getProtein());
        assertTrue(result.getDeficientNutrients().isEmpty());
        assertTrue(result.getNextMealSuggestion().isEmpty());
        verify(restTemplate).postForObject(anyString(), any(), eq(DietAnalysisResponse.class));
    }

    @Test
    void fetchFastApiResponse_예외_발생() {
        // 준비
        when(restTemplate.postForObject(anyString(), any(), eq(DietAnalysisResponse.class)))
                .thenThrow(new RuntimeException("API 호출 실패"));

        // 실행
        DietAnalysisResponse result = dietAnalysisService.fetchFastApiResponse(request);

        // 검증
        assertNotNull(result);
        assertTrue(result.getFoodList().isEmpty());
        assertTrue(result.getNutritionPerFood().isEmpty());
        assertEquals(0.0, result.getTotalNutrition().getProtein());
        assertTrue(result.getDeficientNutrients().isEmpty());
        assertTrue(result.getNextMealSuggestion().isEmpty());
        verify(restTemplate).postForObject(anyString(), any(), eq(DietAnalysisResponse.class));
    }

    @Test
    void saveDietAnalysis_성공() {
        // 준비
        when(consumedFoodRepository.save(any(ConsumedFood.class))).thenReturn(new ConsumedFood());
        when(deficientNutrientRepository.save(any(DeficientNutrient.class))).thenReturn(new DeficientNutrient());
        when(recommendedMealRepository.save(any(RecommendedMeal.class))).thenReturn(new RecommendedMeal());

        // 실행
        dietAnalysisService.saveDietAnalysis(request, response);

        // 검증
        verify(consumedFoodRepository, times(1)).save(any(ConsumedFood.class));
        verify(deficientNutrientRepository, times(1)).save(any(DeficientNutrient.class));
        verify(recommendedMealRepository, times(2)).save(any(RecommendedMeal.class));
    }

    @Test
    void saveDietAnalysis_영양정보_누락() {
        // 준비
        DietAnalysisResponse nullNutritionResponse = new DietAnalysisResponse(
                Arrays.asList("사과"),
                null,
                new Nutrition(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                Arrays.asList("단백질"),
                Arrays.asList("샐러드")
        );
        when(deficientNutrientRepository.save(any(DeficientNutrient.class))).thenReturn(new DeficientNutrient());
        when(recommendedMealRepository.save(any(RecommendedMeal.class))).thenReturn(new RecommendedMeal());

        // 실행
        dietAnalysisService.saveDietAnalysis(request, nullNutritionResponse);

        // 검증
        verify(consumedFoodRepository, never()).save(any(ConsumedFood.class));
        verify(deficientNutrientRepository, times(1)).save(any(DeficientNutrient.class));
        verify(recommendedMealRepository, times(1)).save(any(RecommendedMeal.class));
    }

    @Test
    void getDietAnalysisResponse_성공() {
        // 준비
        when(restTemplate.postForObject(anyString(), any(), eq(DietAnalysisResponse.class)))
                .thenReturn(response);

        // 실행
        DietAnalysisResponse result = dietAnalysisService.getDietAnalysisResponse(request);

        // 검증
        assertNotNull(result);
        assertEquals(2, result.getFoodList().size());
        assertEquals(1, result.getNutritionPerFood().size());
        verify(restTemplate).postForObject(anyString(), any(), eq(DietAnalysisResponse.class));
    }

}