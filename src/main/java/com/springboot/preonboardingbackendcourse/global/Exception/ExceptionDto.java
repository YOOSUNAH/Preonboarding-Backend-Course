package com.springboot.preonboardingbackendcourse.global.Exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class ExceptionDto {

    private int statusCode;
    private HttpStatus state;
    private String message;
}
