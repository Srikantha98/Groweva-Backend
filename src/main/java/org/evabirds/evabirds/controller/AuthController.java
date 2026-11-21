package org.evabirds.evabirds.controller;

import jakarta.validation.Valid;
import org.evabirds.evabirds.dto.AuthResponse;
import org.evabirds.evabirds.dto.LoginRequest;
import org.evabirds.evabirds.dto.RegisterRequest;
import org.evabirds.evabirds.dto.SendOtpRequest;
import org.evabirds.evabirds.dto.VerifyOtpRequest;
import org.evabirds.evabirds.service.OtpService;
import org.evabirds.evabirds.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    public AuthController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendRegistrationOtp(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Verification code sent to " + request.getEmail()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean valid = otpService.verifyRegistrationOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(Map.of("verified", valid, "message", "Email verified successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
