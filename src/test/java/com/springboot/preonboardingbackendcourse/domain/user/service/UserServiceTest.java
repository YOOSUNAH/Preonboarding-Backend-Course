package com.springboot.preonboardingbackendcourse.domain.user.service;

import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.LOGIN_REQUEST_DTO;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.SIGNUP_REQUEST_DTO;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_SERVLET_RESPONSE;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_USER;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_USER_NAME;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_USER_NICKNAME;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_USER_PASSWORD;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TEST_USER_ROLE;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.TOKEN;
import static com.springboot.preonboardingbackendcourse.test.UserCommonTest.WRONG_LOGIN_REQUEST_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.LoginResponse;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response.SignupResponse;
import com.springboot.preonboardingbackendcourse.domain.user.entity.User;
import com.springboot.preonboardingbackendcourse.domain.user.repository.UserRepository;
import com.springboot.preonboardingbackendcourse.support.jwt.JwtUtil;
import jakarta.persistence.EntityExistsException;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtUtil jwtUtil;

    @Nested
    @DisplayName("회원 가입 관련 테스트")
    class SignUpTest {

        @DisplayName("회원 가입 V1")
        @Test
        void signup() {
            // given
            userRepository.deleteAll();
            // when
            SignupResponse response = userService.signup(SIGNUP_REQUEST_DTO);
            // then
            User savedUser = userRepository.findByUsername(SIGNUP_REQUEST_DTO.getUsername())
                .orElse(null);
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getUsername()).isEqualTo(SIGNUP_REQUEST_DTO.getUsername());
            assertThat(passwordEncoder.matches(SIGNUP_REQUEST_DTO.getPassword(),
                savedUser.getPassword())).isTrue();
            assertThat(savedUser.getNickname()).isEqualTo(SIGNUP_REQUEST_DTO.getNickname());
            assertThat(savedUser.getRole()).isEqualTo(SIGNUP_REQUEST_DTO.getRole());
        }

        @DisplayName("회원 가입 V2")
        @Test
        void signup2() {
            // given
            userRepository.deleteAll();
            // when
            assertDoesNotThrow(
                () -> userService.signup(SIGNUP_REQUEST_DTO)
            );

            // then
            assertTrue(userRepository.existsByUsername(SIGNUP_REQUEST_DTO.getUsername()));
        }

        @DisplayName("회원 가입 실패 - 중복된 사용자")
        @Test
        void signup_fail_duplicateUser() {
            // given
            userRepository.save(
                new User(TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_NICKNAME, TEST_USER_ROLE));

            assertTrue(userRepository.existsByUsername(TEST_USER_NAME));

            // when, then
            assertThrows(EntityExistsException.class,
                () -> userService.signup(SIGNUP_REQUEST_DTO)
            );
        }

        @DisplayName("회원 가입 실패 - 중복된 Nickname")
        @Test
        void signup_fail_duplicateNickname() {
            // given
            userRepository.save(
                new User(TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_NICKNAME, TEST_USER_ROLE));

            assertTrue(userRepository.existsByNickname(TEST_USER_NICKNAME));

            // when, then
            assertThrows(EntityExistsException.class,
                () -> userService.signup(SIGNUP_REQUEST_DTO)
            );
        }
    }

    @Nested
    @DisplayName("로그인 관련 테스트")
    class LoginTest {

        @DisplayName("로그인")
        @Test
        void login() {
            // given
            String encodedPassword = passwordEncoder.encode(TEST_USER.getPassword());
            User userWithEncodedPassword = new User(
                TEST_USER.getUsername(),
                encodedPassword,
                TEST_USER.getNickname(),
                TEST_USER.getRole()
            );
            userRepository.save(userWithEncodedPassword);

            // when
            when(jwtUtil.generateAccessAndRefreshToken(
                userWithEncodedPassword.getUserId(),
                userWithEncodedPassword.getRole())
            ).thenReturn(TOKEN);

            // when
            LoginResponse response = userService.login(LOGIN_REQUEST_DTO, TEST_SERVLET_RESPONSE);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo(TOKEN);
            verify(TEST_SERVLET_RESPONSE).addHeader(JwtUtil.AUTHORIZATION_HEADER, TOKEN);
        }

        @DisplayName("로그인 실패 - user가 존재하지 않을때")
        @Test
        void login_fail() {
            // given
            assertFalse(userRepository.findByUsername(TEST_USER_NAME).isPresent());

            // when, then
            assertThrows(UsernameNotFoundException.class,
                () -> userService.login(LOGIN_REQUEST_DTO, TEST_SERVLET_RESPONSE)
            );
        }

        @DisplayName("로그인 실패 - password가 일치하지 않을때")
        @Test
        void login_fail_by_password() {
            // given
            String encodedPassword = passwordEncoder.encode(TEST_USER.getPassword());
            User userWithEncodedPassword = new User(
                TEST_USER.getUsername(),
                encodedPassword,
                TEST_USER.getNickname(),
                TEST_USER.getRole()
            );
            userRepository.save(userWithEncodedPassword);

            // when, then
            assertThrows(NoSuchElementException.class,
                () -> userService.login(WRONG_LOGIN_REQUEST_DTO, TEST_SERVLET_RESPONSE)
            );
        }
    }
}
