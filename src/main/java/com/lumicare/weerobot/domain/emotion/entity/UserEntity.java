package com.lumicare.weerobot.domain.emotion.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String occupation;

    @Column
    private String concern;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 로그인용
    public UserEntity(String id, String password) {
        this.id = id;
        this.password = password;
    }

    // 회원가입용 (필수 값)
    public UserEntity(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public enum Gender {
        MALE,
        FEMALE
    }


}
