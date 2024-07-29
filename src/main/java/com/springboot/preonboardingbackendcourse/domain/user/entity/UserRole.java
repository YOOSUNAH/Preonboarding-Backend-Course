package com.springboot.preonboardingbackendcourse.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static class Authority {

        private static final String USER = "ROLE_USER";
        private static final String ADMIN = "ROLE_ADMIN";
    }
}
