package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.TextRequest;
import com.lumicare.weerobot.domain.emotion.dto.TextEmotionResponse;
import com.lumicare.weerobot.domain.emotion.service.TextEmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/emotion")
@CrossOrigin(origins = "*")
public class TextEmotionController {

    private final TextEmotionService emotionService;

    public TextEmotionController(TextEmotionService emotionService) {
        this.emotionService = emotionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeEmotion(@RequestBody TextRequest request) {
        // FastAPI로부터 원본 감정 결과 받기
        TextEmotionResponse rawResponse = emotionService.sendToFastAPI(request);
        Map<String, Float> originalEmotion = rawResponse.getEmotion();

        // 감정 매핑 정의
        Map<String, String> emotionMapping = new HashMap<>();
        emotionMapping.put("happy", "HAPPY");
        emotionMapping.put("sad", "SAD");
        emotionMapping.put("heartache", "SAD");
        emotionMapping.put("angry", "ANGRY");
        emotionMapping.put("embarrassed", "SURPRISE");
        emotionMapping.put("anxious", "FEAR");

        // 범주별 점수 누적
        Map<String, Float> mappedEmotion = new HashMap<>();
        for (Map.Entry<String, Float> entry : originalEmotion.entrySet()) {
            String original = entry.getKey();
            Float score = entry.getValue();

            String mapped = emotionMapping.getOrDefault(original, "NEUTRAL"); // default: NEUTRAL
            mappedEmotion.put(mapped, mappedEmotion.getOrDefault(mapped, 0.0f) + score);
        }

        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("mappedEmotion", mappedEmotion);  // 치환된 감정 결과
        response.put("rawEmotion", originalEmotion);    // 원본 감정 결과도 포함

        return ResponseEntity.ok(response);
    }
}
