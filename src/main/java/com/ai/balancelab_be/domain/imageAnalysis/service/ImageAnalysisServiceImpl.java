package com.ai.balancelab_be.domain.imageAnalysis.service;

import org.springframework.stereotype.Service;

@Service
public class ImageAnalysisServiceImpl implements ImageAnalysisService {
    @Override
    public String analyzeDiet(String userId, String filePath) {
        return "Analysis result";
    }
}