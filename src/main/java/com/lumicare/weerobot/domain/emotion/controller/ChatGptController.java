package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.EmotionRequest;
import com.lumicare.weerobot.domain.emotion.service.ChatGptService;
import org.springframework.web.bind.annotation.*;
import com.lumicare.weerobot.domain.emotion.entity.UserEntity;
import com.lumicare.weerobot.domain.emotion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatGptController {

    private final ChatGptService chatGptService;
    private final UserRepository userRepository;

    @PostMapping("/advice")
    public String getChatAdvice(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String emotion = request.get("emotion");

        // 1. DB에서 사용자 정보 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 2. GPT로 조언 생성
        return chatGptService.getAdviceByEmotion(user, emotion);
    }
}