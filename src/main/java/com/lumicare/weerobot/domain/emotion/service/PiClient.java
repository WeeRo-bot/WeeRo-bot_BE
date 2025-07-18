package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.dto.PiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "piClient", url = "${pi.base-url}")
public interface PiClient {
    @PostMapping("/analyze")
    PiResponseDto analyze();
}
