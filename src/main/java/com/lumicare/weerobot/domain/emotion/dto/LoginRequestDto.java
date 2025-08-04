package com.lumicare.weerobot.domain.emotion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    @NotBlank(message = "아이디")
    private String id;

    @NotBlank(message = "비밀번호")
    private String password;

}