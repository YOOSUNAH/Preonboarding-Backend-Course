package com.springboot.preonboardingbackendcourse.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    private String accessToken;

    @Column(nullable = false)
    private Boolean isExpired;

    public Token(String tokenValue, boolean isExpired) {
        this.accessToken = tokenValue;
        this.isExpired = isExpired;
    }
}
