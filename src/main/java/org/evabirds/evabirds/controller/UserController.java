package org.evabirds.evabirds.controller;

import jakarta.validation.Valid;
import org.evabirds.evabirds.dto.AuthResponse;
import org.evabirds.evabirds.dto.LoginRequest;
import org.evabirds.evabirds.dto.RegisterRequest;
import org.evabirds.evabirds.dto.UserResponse;
import org.evabirds.evabirds.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final org.evabirds.evabirds.service.OtpService otpService;

    public UserController(UserService userService, org.evabirds.evabirds.service.OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
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

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @PatchMapping("/{id}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role
    ) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @PutMapping("/change-password")
    public ResponseEntity<java.util.Map<String, String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody org.evabirds.evabirds.dto.ChangePasswordRequest request
    ) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(java.util.Map.of("message", "Password changed successfully"));
    }

    @PostMapping("/send-email-change-otp")
    public ResponseEntity<java.util.Map<String, String>> sendEmailChangeOtp(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody org.evabirds.evabirds.dto.SendOtpRequest request
    ) {
        otpService.sendEmailChangeOtp(userDetails.getUsername(), request.getEmail());
        return ResponseEntity.ok(java.util.Map.of("message", "Verification code sent to " + request.getEmail()));
    }

    @PutMapping("/update-email")
    public ResponseEntity<AuthResponse> updateEmail(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody org.evabirds.evabirds.dto.UpdateEmailRequest request
    ) {
        AuthResponse response = userService.updateEmail(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }
}