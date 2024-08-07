package com.springboot.preonboardingbackendcourse.domain.user.controller.dto.request;

import com.springboot.preonboardingbackendcourse.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SignupRequest {

    private String username;
    private String password;
    private String nickname;
    private UserRole role;
}
