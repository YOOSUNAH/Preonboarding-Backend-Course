package com.springboot.preonboardingbackendcourse.domain.user.dto;

import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import lombok.Getter;


@Getter
public class SignupRequest {

    private String username;
    private String password;
    private String nickname;
    private UserRole role;
}
