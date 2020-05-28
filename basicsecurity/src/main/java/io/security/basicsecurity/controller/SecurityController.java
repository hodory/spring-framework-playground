package io.security.basicsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
    @GetMapping("/")
    public String index() {
        return "It works";
    }

    @GetMapping("/loginPage")
    public String loginPage() {
        return "loginPage";
    }
}
