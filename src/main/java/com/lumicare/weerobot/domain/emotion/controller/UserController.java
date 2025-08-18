package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.*;
import com.lumicare.weerobot.domain.emotion.exception.DuplicateIDException;
import com.lumicare.weerobot.domain.emotion.repository.UserRepository;
import com.lumicare.weerobot.domain.emotion.security.JwtTokenProvider;
import com.lumicare.weerobot.domain.emotion.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 아이디 중복 체크
    @PostMapping("/checkId")
    public ResponseEntity<ResponseDto<Map<String, Object>>> checkId(@RequestBody Map<String, String> requestBody) {
        String id = requestBody.get("id");
        boolean exists = userRepository.existsById(id);

        Map<String, Object> data = Map.of(
                "available", !exists,
                "message", exists ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다."
        );

        ResponseDto<Map<String, Object>> response =
                new ResponseDto<>(200, true, "아이디 중복 확인 결과", data);

        return ResponseEntity.ok(response);
    }


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Integer>> signup(@Valid @RequestBody SignupRequestDto request){
        userService.signup(request);

        ResponseDto<Integer> response = new ResponseDto<>(200, true, "회원가입 성공", 1);

        return ResponseEntity.ok().body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto request){
        TokenResponseDto token = userService.login(request);

        ResponseDto<TokenResponseDto> response = new ResponseDto<>(200, true, "로그인 성공", token);

        return ResponseEntity.ok(response);
    }

    // 프로필 조회
    @GetMapping("/profiles")
    public ResponseEntity<ResponseDto<ProfilesResponseDto>> getUserInfo(@RequestHeader("Authorization") String token){
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        ProfilesResponseDto userInfo = userService.getUserInfo(userId);;

        ResponseDto<ProfilesResponseDto> response = new ResponseDto<>(200, true, "프로필 조회 성공", userInfo);

        return ResponseEntity.ok(response);
    }
}
