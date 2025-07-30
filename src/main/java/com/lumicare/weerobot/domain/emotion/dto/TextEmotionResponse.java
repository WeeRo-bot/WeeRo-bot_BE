package com.lumicare.weerobot.domain.emotion.dto;

import java.util.Map;

public class TextEmotionResponse {
    private Map<String, Float> emotion;

    public Map<String, Float> getEmotion() {
        return emotion;
    }

    public void setEmotion(Map<String, Float> emotion) {
        this.emotion = emotion;
    }
}

