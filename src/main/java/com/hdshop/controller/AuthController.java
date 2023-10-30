package com.hdshop.controller;

import com.hdshop.dto.auth.JwtAuthResponse;
import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register new account")
    @PostMapping(value = {"/signup", "/register"})
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return new ResponseEntity<>(authService.register(registerDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Login account")
    @PostMapping(value = {"/signin", "/login"})
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        String accessToken = authService.login(loginDTO);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(jwtAuthResponse);
    }
}
