package com.springboot.preonboardingbackendcourse.domain.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/v1")
public class PageController{

    @GetMapping("/homePage")
    public String homepage() {
        return "home";
    }

    @GetMapping("/signupPage")
    public String signupPage() {
        log.info("#### signupPage controller");
        return "signup";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "login";
    }
}

