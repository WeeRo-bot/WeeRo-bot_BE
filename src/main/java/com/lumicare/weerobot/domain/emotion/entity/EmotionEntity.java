package com.lumicare.weerobot.domain.emotion.entity;

import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "emotion")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionEntity {
    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_id")
    private Long emotionId;

    // 감정
    @Enumerated(EnumType.STRING)
    @Column(name = "emotion", nullable = false, length = 20)
    private EmotionType emotion;

    // 신뢰도
    @Column(name = "confidence", precision = 5, scale = 4, nullable = false)
    private BigDecimal confidence; // 0.0000 ~ 1.0000

    // 측정 일시
    @CreationTimestamp
    @Column(name = "captured_at", nullable = false, updatable = false)
    private LocalDateTime capturedAt;

//    // Device
//    @OneToOne //(fetch = FetchType.LAZY)
//    @JoinColumn(name = "device_id", nullable = true)
//    private Device device;
//
//    // Member
//    @ManyToOne //(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = true)
//    private Member user;

    // Builder
    @Builder
    public EmotionEntity(EmotionType emotion, BigDecimal confidence/*, Member user, Device device*/) {
        this.emotion = emotion;
        this.confidence = confidence;
//        this.device = device;
//        this.user = user;
    }
}
