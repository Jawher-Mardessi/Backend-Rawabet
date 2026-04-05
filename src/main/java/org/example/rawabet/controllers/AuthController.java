package org.example.rawabet.controllers;

import org.example.rawabet.dto.LoginRequest;
import org.example.rawabet.services.ServiceImpl.user.AuthServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @PostMapping("/test")
    public String test() {
        return "OK";
    }
}