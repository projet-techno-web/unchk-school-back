package com.unchk.unchkBackend.controller;


import com.unchk.unchkBackend.dto.user.LoginRequest;
import com.unchk.unchkBackend.dto.user.LoginResponse;
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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = authService.register(request);
        if (result.equals("Utilisateur inscrit avec succès !")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }



    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @RequestParam("token") String token,
        @RequestBody ResetPasswordRequest request
    ) {
        String response = authService.resetPassword(token, request);
        return ResponseEntity.ok(response);
    }
}
