package org.example.rawabet.controllers;

import lombok.RequiredArgsConstructor;
import org.example.rawabet.services.AuthServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password) {

        return authService.loginAndGenerateToken(email, password);
    }
    @PostMapping("/test")
    public String test() {
        return "OK";
    }
}