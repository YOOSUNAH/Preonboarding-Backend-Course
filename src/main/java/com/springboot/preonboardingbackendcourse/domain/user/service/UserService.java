package com.springboot.preonboardingbackendcourse.domain.user.service;

import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.LoginRequest;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.LoginResponse;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.SignupRequest;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.SignupResponse;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.SignupResponse.Authority;
import com.springboot.preonboardingbackendcourse.domain.user.entity.User;
import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.springboot.preonboardingbackendcourse.domain.user.repository.UserRepository;
import com.springboot.preonboardingbackendcourse.support.jwt.JwtUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String nickname = requestDto.getNickname();
        UserRole role = requestDto.getRole() != null ? requestDto.getRole() : UserRole.USER;
        validateUserDuplicate(username, nickname);

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, nickname, role);
        userRepository.save(user);

        List<Authority> authorities = List.of(new SignupResponse.Authority(role.getAuthority()));
        return new SignupResponse(user.getUsername(), user.getNickname(), authorities);
    }

    @Transactional
    public LoginResponse login(LoginRequest requestDto, HttpServletResponse response) {
        User user = validateUser(requestDto);
        Long userId = user.getUserId();
        UserRole role = user.getRole();

        String accessToken = jwtUtil.generateAccessAndRefreshToken(userId, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        return new LoginResponse(accessToken);
    }

    private void validateUserDuplicate(String username, String nickname) {
        if (userRepository.existsByUsername(username)) {
            throw new EntityExistsException("해당 사용자가 이미 존재합니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new EntityExistsException("해당 이메일이 이미 존재합니다. ");
        }
    }

    private User validateUser(LoginRequest requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
            () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new NoSuchElementException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

}
