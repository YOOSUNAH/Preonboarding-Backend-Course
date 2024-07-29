package com.springboot.preonboardingbackendcourse.global.Exception;


import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handBadRequestException(Exception e) {
        return createResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({
        NullPointerException.class,
        NoSuchElementException.class,
        UsernameNotFoundException.class,
        EntityNotFoundException.class
    })
    public ResponseEntity<ExceptionDto> handleNotFoundException(Exception e) {
        return createResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }



    private ResponseEntity<ExceptionDto> createResponse(HttpStatus status, String message) {
        return ExceptionDto.of(status, message);
    }

}
