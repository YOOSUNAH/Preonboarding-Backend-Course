package com.springboot.preonboardingbackendcourse.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.preonboardingbackendcourse.domain.user.dto.LoginRequest;
import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.springboot.preonboardingbackendcourse.global.jwt.JwtUtil;
import com.springboot.preonboardingbackendcourse.global.security.UserDetailsImpl;
import com.springboot.preonboardingbackendcourse.global.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        setFilterProcessesUrl("/v1/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequest requestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequest.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.getUsername(),
                    requestDto.getPassword(),
                    null
                )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRole role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(Long.valueOf(username), role);
        userDetailsService.setLoginUserByToken(token);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

}

