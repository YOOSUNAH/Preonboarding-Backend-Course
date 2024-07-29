package com.springboot.preonboardingbackendcourse.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static UserRole getRoleByAuthority(String authority){
        return authority.equals(ADMIN.authority) ? UserRole.ADMIN : UserRole.USER;
    }
}
