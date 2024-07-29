package com.springboot.preonboardingbackendcourse.global.filter;

import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.springboot.preonboardingbackendcourse.global.jwt.JwtUtil;
import com.springboot.preonboardingbackendcourse.global.jwt.RefreshTokenRepository;
import com.springboot.preonboardingbackendcourse.global.jwt.TokenState;
import com.springboot.preonboardingbackendcourse.global.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String tokenValue = jwtUtil.getJwtFromHeader(request);
        if (tokenValue == null) {
            filterChain.doFilter(request, response);
            return;
        }

        handleTokenValidation(tokenValue, response);
        filterChain.doFilter(request, response);
    }

    private void handleTokenValidation(String tokenValue, HttpServletResponse response) {
        TokenState state = jwtUtil.validateToken(tokenValue);

        if (state.equals(TokenState.INVALID)) {
            log.error("Token Error");
        }
        else if (state.equals(TokenState.EXPIRED)) {
            handleExpiredToken(tokenValue, response);
        }
        else {
            handleValidToken(tokenValue);
        }
    }

    private void handleExpiredToken(String tokenValue, HttpServletResponse response) {
        try {
            Claims info = jwtUtil.getUserInfoFromExpiredToken(tokenValue);
            Long userId = Long.parseLong(info.getSubject());

            if (refreshTokenRepository.existsByUserId(userId)) {
                UserRole role = info.get(JwtUtil.AUTHORIZATION_KEY)
                    .equals("ROLE_ADMIN") ? UserRole.ADMIN : UserRole.USER;

                String newToken = jwtUtil.regenerateAccessToken(userId, role);
                log.info("Acces Token이 재발급되었습니다.");
                response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newToken);
                response.setStatus(HttpServletResponse.SC_OK);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.info("Acces Token, Refresh Token 모두 만료되었습니다. 다시 로그인해주세요.");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void handleValidToken(String tokenValue) {
        Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
        Long userId = Long.parseLong(info.getSubject());
        UserRole role = info.get(JwtUtil.AUTHORIZATION_KEY)
            .equals("ROLE_ADMIN") ? UserRole.ADMIN : UserRole.USER;
        setAuthentication(userId, role);
    }

    public void setAuthentication(Long userId, UserRole tokenValue) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(userId, tokenValue);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(Long userId, UserRole role) {
        UserDetails userDetails = userDetailsService.getUserDetails(userId, role);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }
}
