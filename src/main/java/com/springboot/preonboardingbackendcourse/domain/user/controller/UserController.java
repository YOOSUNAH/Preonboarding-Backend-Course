package com.springboot.preonboardingbackendcourse.domain.user.controller;

import static com.springboot.preonboardingbackendcourse.support.jwt.JwtUtil.AUTHORIZATION_HEADER;

import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.LoginRequest;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.SignupRequest;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.LoginResponse;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.SignupResponse;
import com.springboot.preonboardingbackendcourse.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public SignupResponse signup(
        @Valid @RequestBody SignupRequest requestDto
    ) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public LoginResponse login(
        @Valid @RequestBody LoginRequest requestDto,
        HttpServletResponse response
    ) {
        return userService.login(requestDto, response);
    }

    @GetMapping("/get-session")
    public String getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String value = (String) session.getAttribute(
            AUTHORIZATION_HEADER);
        return value;
    }

}
