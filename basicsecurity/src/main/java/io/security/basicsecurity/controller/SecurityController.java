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

    @GetMapping("/user")
    public String user() {
        return "User";
    }

    @GetMapping("/admin/billing")
    public String adminBilling() {
        return "Admin Billing";
    }

    @GetMapping("/admin")
    public String adminAll() {
        return "Admin All";
    }

    @GetMapping("/denied")
    public String denied() {
        return "denied";
    }

    @GetMapping("/login")
    public String login() {
        return "Login Page";
    }
}
