package com.ai.balancelab_be.controller;

import com.ai.balancelab_be.dto.FoodNameRequest;
import com.ai.balancelab_be.service.DietAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DietAnalysisController {

    @Autowired
    private DietAnalysisService dietAnalysisService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/foodName")
    public String sendMessage(@RequestParam("message") String message, Model model) {
        FoodNameRequest foodNameRequest = new FoodNameRequest(message);
        List<String> food_list = dietAnalysisService.getFoodNameResponse(foodNameRequest);
        model.addAttribute("message", message);
        model.addAttribute("food_list", food_list);
        System.out.println("확인확인: " + food_list);
        return "index";
    }

}
