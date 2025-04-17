package com.ai.balancelab_be.domain.imageAnalysis.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public interface ImageAnalysisService {
    Map<String, Object> analyzeDiet(Long userId, String filePath);
}