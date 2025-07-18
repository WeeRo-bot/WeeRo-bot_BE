package com.lumicare.weerobot.domain.emotion.dto;

import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PiResponseDto {
    private EmotionType emotion;
    private BigDecimal confidence;
}
