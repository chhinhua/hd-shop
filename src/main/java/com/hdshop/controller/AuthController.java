package com.hdshop.controller;

import com.hdshop.dto.auth.LoginDTO;
import com.hdshop.dto.auth.LoginResponse;
import com.hdshop.dto.auth.RegisterDTO;
import com.hdshop.dto.auth.VerifyOtpRequest;
import com.hdshop.security.JwtTokenProvider;
import com.hdshop.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "Sinup new account")
    @PostMapping(value = {"/signup", "/register"})
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return new ResponseEntity<>(authService.register(registerDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Signin account")
    @PostMapping(value = {"/signin", "/login"})
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponse response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "Signin account")
//    @PostMapping(value = {"/admin/signin", "/admin/login"})
//    public ResponseEntity<LoginResponse> loginForAdmin(@Valid @RequestBody LoginDTO loginDTO) {
//        LoginResponse response = authService.login(loginDTO);
//        return ResponseEntity.ok(response);
//    }

    @Operation(summary = "Send OTP by email address")
    @PostMapping("/otp/send")
    public ResponseEntity<String> sendOtpByEmail(@RequestParam String email) {
        return ResponseEntity.ok(authService.sendOTP_ByEmail(email));
    }

    @Operation(summary = "Send OTP by username")
    @GetMapping("/otp/send")
    public ResponseEntity<String> sendOtpByUsername(@RequestParam String username) {
        return ResponseEntity.ok(authService.sendOTP_ByUsername(username));
    }

    @Operation(summary = "Verify OTP")
    @PostMapping("/otp/verify")
    public ResponseEntity<String> sendOtp(@RequestBody VerifyOtpRequest otpRequest) {
        return ResponseEntity.ok(authService.verifyOTP_ByEmail(otpRequest));
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkTokenExpire(@RequestParam("token") String token) {
        return ResponseEntity.ok(jwtTokenProvider.checkExpiredToken(token));
    }
}
