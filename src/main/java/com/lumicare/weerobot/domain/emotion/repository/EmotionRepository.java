package com.lumicare.weerobot.domain.emotion.repository;

import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionRepository extends JpaRepository<EmotionEntity, Long> {

}
