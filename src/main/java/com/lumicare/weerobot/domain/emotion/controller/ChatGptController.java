package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.EmotionRequest;
import com.lumicare.weerobot.domain.emotion.service.ChatGptService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/chat")
public class ChatGptController {

    @Autowired
    private ChatGptService chatGptService;

    @PostMapping("/advice")
    public String getAdvice(@RequestBody EmotionRequest request) {
        return chatGptService.getAdviceByEmotion(request.getEmotion());
    }
}