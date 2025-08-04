package com.lumicare.weerobot.domain.emotion.dto;

import com.lumicare.weerobot.domain.emotion.entity.UserEntity;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {

    @NotBlank(message = "아이디")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 문자와 숫자만 가능합니다.")
    private String id;

    @NotBlank(message = "비밀번호")
    private String password;

    @NotBlank(message = "비밀번호 확인")
    private String passwordConfirm;  // 비밀번호 확인용 필드

    private String name; // 닉네임

    private int age; // 나이

    private UserEntity.Gender gender; // 성별

    private String occupation; // 직업

    private String concern; // 고민
}
