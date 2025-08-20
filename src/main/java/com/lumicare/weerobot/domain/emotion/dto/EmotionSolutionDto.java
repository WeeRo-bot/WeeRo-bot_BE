package com.lumicare.weerobot.domain.emotion.dto;

import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionSolutionDto {
    private EmotionType mainEmotion;
    private String solution;
}
