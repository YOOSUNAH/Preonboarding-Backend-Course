package com.springboot.preonboardingbackendcourse.global.filter;

import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import com.springboot.preonboardingbackendcourse.support.jwt.JwtUtil;
import com.springboot.preonboardingbackendcourse.support.jwt.RefreshTokenRepository;
import com.springboot.preonboardingbackendcourse.support.jwt.TokenState;
import com.springboot.preonboardingbackendcourse.global.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import io.jsonwebtoken.Claims;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilter(jwtAuthorizationFilter)
            .build();
    }

    @Test
    public void shouldSkipFilterWhenTokenIsMissing() throws Exception {
        given(jwtUtil.getJwtFromHeader(any())).willReturn(null);

        mockMvc.perform(get("/api/protected"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowAccessWithValidToken() throws Exception {
        given(jwtUtil.getJwtFromHeader(any())).willReturn("validToken");
        given(jwtUtil.validateToken("validToken")).willReturn(TokenState.VALID);
        given(jwtUtil.getUserInfoFromToken("validToken")).willReturn(generateValidClaims());

        mockMvc.perform(get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer validToken"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldRefreshTokenWhenExpired() throws Exception {
        given(jwtUtil.getJwtFromHeader(any())).willReturn("expiredToken");
        given(jwtUtil.validateToken("expiredToken")).willReturn(TokenState.EXPIRED);
        given(jwtUtil.getUserInfoFromExpiredToken("expiredToken")).willReturn(generateExpiredClaims());
        given(refreshTokenRepository.existsByUserId(1L)).willReturn(true);
        given(jwtUtil.regenerateAccessToken(1L, UserRole.USER)).willReturn("newToken");

        mockMvc.perform(get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer expiredToken"))
            .andExpect(status().isOk())
            .andExpect(result ->
                assertTrue(result.getResponse().getHeader(HttpHeaders.AUTHORIZATION).contains("newToken")));
    }

    @Test
    public void shouldRejectRequestWithInvalidToken() throws Exception {
        given(jwtUtil.getJwtFromHeader(any())).willReturn("invalidToken");
        given(jwtUtil.validateToken("invalidToken")).willReturn(TokenState.INVALID);

        mockMvc.perform(get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldRejectRequestWhenBothTokensInvalid() throws Exception {
        given(jwtUtil.getJwtFromHeader(any())).willReturn("expiredToken");
        given(jwtUtil.validateToken("expiredToken")).willReturn(TokenState.EXPIRED);
        given(jwtUtil.getUserInfoFromExpiredToken("expiredToken")).willReturn(generateExpiredClaims());
        given(refreshTokenRepository.existsByUserId(1L)).willReturn(false);

        mockMvc.perform(get("/api/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer expiredToken"))
            .andExpect(status().isUnauthorized());
    }

    private Claims generateValidClaims() {
        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("1");
        given(claims.get(JwtUtil.AUTHORIZATION_KEY)).willReturn("ROLE_USER");
        return claims;
    }

    private Claims generateExpiredClaims() {
        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("1");
        given(claims.get(JwtUtil.AUTHORIZATION_KEY)).willReturn("ROLE_USER");
        return claims;
    }
}
