package com.springboot.preonboardingbackendcourse.domain.user.controller.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private String username;
    private String nickname;
    private List<Authority> authorities;

    @Getter
    @AllArgsConstructor
    public static class Authority {
        private String authorityName;
    }
}
