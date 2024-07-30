package com.springboot.preonboardingbackendcourse.domain.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/v1")
public class PageController implements ErrorController{

    @GetMapping("/homePage")
    public String homepage() {
        log.info("Accessing homepage");
        return "home";
    }

    @GetMapping("/signupPage")
    public String signupPage() {
        log.info("Accessing signupPage");
        return "signup";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        log.info("Accessing loginPage");
        return "login";
    }
}

