package com.springboot.preonboardingbackendcourse.domain.user.dto;

import com.springboot.preonboardingbackendcourse.domain.user.entity.Authority;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private String username;
    private String nickname;
    private List<Authority> authorities;

}
