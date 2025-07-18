package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.dto.PiResponseDto;
import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import com.lumicare.weerobot.domain.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final PiClient piClient;

    // [ RPI에 감정 분석 API 호출 -> 분석 결과 반환 ]
    @Transactional
    public EmotionEntity analyzeEmotion() {
        // 감정 분석 API 호출
        PiResponseDto res = piClient.analyze();

        // Entity 생성 및 저장
        EmotionEntity emotion = EmotionEntity.builder()
                .emotion(res.getEmotion())
                .confidence(res.getConfidence())
                .build();

        EmotionEntity saved = emotionRepository.save(emotion);

        return saved;
    }
}
