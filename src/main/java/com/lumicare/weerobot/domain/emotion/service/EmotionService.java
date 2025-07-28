package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.dto.EmotionResultDto;
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
    public EmotionResultDto analyzeEmotion() {
        // 라즈베리파이 API 호출
        PiResponseDto pi = piClient.analyze();

        // 오류가 있으면 저장하지 않고 바로 반환
        if (pi.getError() != null) {
            log.warn("[EmotionService] Pi 오류 응답: {}", pi.getError());
            return EmotionResultDto.builder()
                    .emotion(pi.getEmotion())
                    .confidence(pi.getConfidence())
                    .advice(pi.getAdvice())
                    .error(pi.getError())
                    .build();
        }

        // Entity 생성 및 저장
        EmotionEntity emotion = EmotionEntity.builder()
                .emotion(pi.getEmotion())
                .confidence(pi.getConfidence())
                .build();

        EmotionEntity saved = emotionRepository.save(emotion);

        // EmotionResultDto 반환
        return EmotionResultDto.builder()
                .id(saved.getEmotionId())
                .emotion(saved.getEmotion())
                .confidence(saved.getConfidence())
                .capturedAt(saved.getCapturedAt())
                .advice(pi.getAdvice())
                .error(pi.getError())
                .build();
    }
}
