package com.springboot.preonboardingbackendcourse.test;

import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.LoginRequest;
import com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request.SignupRequest;
import com.springboot.preonboardingbackendcourse.domain.user.entity.User;
import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;

public interface UserCommonTest {

    String TEST_USER_NAME = "username1";
    String TEST_USER_PASSWORD = "password12^^";
    String TEST_USER_NICKNAME = "nickname";
    UserRole TEST_USER_ROLE = UserRole.USER;
    HttpServletResponse TEST_SERVLET_RESPONSE = Mockito.mock(HttpServletResponse.class);
    String TOKEN = "test-token";
    String TEST_WRONG_PASSWORD = "password12";

    User TEST_USER = new User(
        TEST_USER_NAME,
        TEST_USER_PASSWORD,
        TEST_USER_NICKNAME,
        TEST_USER_ROLE);

    SignupRequest SIGNUP_REQUEST_DTO = SignupRequest.builder()
        .username(TEST_USER_NAME)
        .password(TEST_USER_PASSWORD)
        .nickname(TEST_USER_NICKNAME)
        .role(TEST_USER_ROLE)
        .build();

    LoginRequest LOGIN_REQUEST_DTO = LoginRequest.builder()
        .username(TEST_USER_NAME)
        .password(TEST_USER_PASSWORD)
        .build();

    LoginRequest WRONG_LOGIN_REQUEST_DTO = LoginRequest.builder()
        .username(TEST_USER_NAME)
        .password(TEST_WRONG_PASSWORD)
        .build();
}
