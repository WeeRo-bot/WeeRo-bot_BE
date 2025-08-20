package com.lumicare.weerobot.domain.emotion.repository;

import com.lumicare.weerobot.domain.emotion.dto.EmotionResultDto;
import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmotionRepository extends JpaRepository<EmotionEntity, Long> {


    List<EmotionEntity> findByCapturedAtBetweenOrderByCapturedAtAsc(LocalDateTime start, LocalDateTime end);

    List<EmotionEntity> findByCapturedAtBetween(LocalDateTime start, LocalDateTime end);
}
