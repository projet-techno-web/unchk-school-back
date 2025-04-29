package com.unchk.unchkBackend.controller;

import com.unchk.unchkBackend.dto.user.LoginRequest;
import com.unchk.unchkBackend.dto.user.RegisterRequest;
import com.unchk.unchkBackend.dto.user.ForgotPasswordRequest;
import com.unchk.unchkBackend.dto.user.ResetPasswordRequest;
import com.unchk.unchkBackend.service.user.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // @PostMapping("/forgot-password")
    // public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    // return authService.forgotPassword(email);
    // }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam("token") String token,
            @RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(token, request);
    }
}
