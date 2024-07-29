package com.springboot.preonboardingbackendcourse.global.Exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
public class ExceptionDto {

    private String message;

    public static ResponseEntity<ExceptionDto> of(
        HttpStatus status, String message
    ) {
        return ResponseEntity.status(status).body(new ExceptionDto(message));
    }
}
