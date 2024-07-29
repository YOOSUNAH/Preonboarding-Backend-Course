package com.springboot.preonboardingbackendcourse.domain.user.service;

import com.springboot.preonboardingbackendcourse.domain.user.dto.LoginRequest;
import com.springboot.preonboardingbackendcourse.domain.user.dto.LoginResponse;
import com.springboot.preonboardingbackendcourse.domain.user.dto.SignupRequest;
import com.springboot.preonboardingbackendcourse.domain.user.dto.SignupResponse;
import com.springboot.preonboardingbackendcourse.domain.user.entity.Authority;
import com.springboot.preonboardingbackendcourse.domain.user.entity.User;
import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.springboot.preonboardingbackendcourse.domain.user.repository.UserRepository;
import com.springboot.preonboardingbackendcourse.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignupResponse signup(SignupRequest requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();
        UserRole role = requestDto.getRole() != null ? requestDto.getRole() : UserRole.USER;

        validateUserDuplicate(username, password, nickname, role);

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, nickname, role);
        userRepository.save(user);

        List<Authority> authorities = List.of(new Authority(role.getAuthority()));
        return new SignupResponse(user.getUsername(), user.getNickname(), authorities);
    }

    private void validateUserDuplicate(String username,String password, String nickname,UserRole role){
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("해당 사용자가 이미 존재합니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("해당 이메일이 이미 존재합니다. ");
        }
    }

    public LoginResponse login(LoginRequest requestDto, HttpServletResponse response) {
        User user = validateUser(requestDto);
        Long userId = user.getUserId();
        UserRole role = user.getRole();
        String accessToken = jwtUtil.createToken(userId, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        return new LoginResponse(accessToken);
    }

    private User validateUser(LoginRequest requestDto){
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
            () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

}