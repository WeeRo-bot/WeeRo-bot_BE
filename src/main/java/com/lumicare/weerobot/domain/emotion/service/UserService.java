package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.dto.LoginRequestDto;
import com.lumicare.weerobot.domain.emotion.dto.ProfilesResponseDto;
import com.lumicare.weerobot.domain.emotion.dto.SignupRequestDto;
import com.lumicare.weerobot.domain.emotion.dto.TokenResponseDto;
import com.lumicare.weerobot.domain.emotion.entity.UserEntity;
import com.lumicare.weerobot.domain.emotion.exception.DuplicateIDException;
import com.lumicare.weerobot.domain.emotion.exception.IncorrectLoginException;
import com.lumicare.weerobot.domain.emotion.repository.UserRepository;
import com.lumicare.weerobot.domain.emotion.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signup(SignupRequestDto request) {
        if (checkId(request.getId())) {
            throw new DuplicateIDException("사용 중인 아이디입니다.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = UserEntity.builder()
                .id(request.getId())
                .password(encodedPassword)
                .name(request.getName())
                .age(request.getAge()) // null 가능
                .gender(request.getGender()) // null 가능
                .occupation(request.getOccupation()) // null 가능
                .concern(request.getConcern()) // null 가능
                .build();

        userRepository.save(user);
    }



    // 아이디 중복 확인
    public boolean checkId(String id) {
        return userRepository.existsUserIdById(id);
    }

    // 로그인
    public TokenResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findUserById(request.getId())
                .orElseThrow(() -> new IncorrectLoginException("아이디가 틀렸습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IncorrectLoginException("비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new TokenResponseDto(token);
    }

    // 프로필 조회
    public ProfilesResponseDto getUserInfo(String id){

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        return new ProfilesResponseDto(
                user.getName()
        );
    }

}
