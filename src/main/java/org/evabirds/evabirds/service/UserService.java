package org.evabirds.evabirds.service;

import org.evabirds.evabirds.dto.AuthResponse;
import org.evabirds.evabirds.dto.LoginRequest;
import org.evabirds.evabirds.dto.RegisterRequest;
import org.evabirds.evabirds.dto.UserResponse;
import org.evabirds.evabirds.entity.Farm;
import org.evabirds.evabirds.entity.Profile;
import org.evabirds.evabirds.entity.User;
import org.evabirds.evabirds.exception.BadRequestException;
import org.evabirds.evabirds.exception.ResourceNotFoundException;
import org.evabirds.evabirds.exception.UserAlreadyExistsException;
import org.evabirds.evabirds.repository.FarmRepository;
import org.evabirds.evabirds.repository.FavoriteRepository;
import org.evabirds.evabirds.repository.MessageRepository;
import org.evabirds.evabirds.repository.NotificationRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.evabirds.evabirds.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final org.evabirds.evabirds.repository.EmployeeRepository employeeRepository;
    private final FarmRepository farmRepository;
    private final FavoriteRepository favoriteRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public UserService(
            UserRepository userRepository,
            org.evabirds.evabirds.repository.EmployeeRepository employeeRepository,
            FarmRepository farmRepository,
            FavoriteRepository favoriteRepository,
            MessageRepository messageRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            OtpService otpService
    ) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.farmRepository = farmRepository;
        this.favoriteRepository = favoriteRepository;
        this.messageRepository = messageRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("Email is already registered: " + email);
        }

        // Verify OTP if supplied or check previous verification
        if (request.getOtp() != null && !request.getOtp().isBlank()) {
            otpService.verifyRegistrationOtp(email, request.getOtp());
        }

        // Evabirds is a unified non-role platform: all users can discover sheds and list sheds
        String role = "ROLE_USER";

        String fullName = request.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = request.getName();
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            fullName = email.contains("@") ? email.split("@")[0] : "User";
        }

        String phone = request.getPhone() != null ? request.getPhone() : request.getPhoneNumber();

        User user = new User(
                email,
                passwordEncoder.encode(request.getPassword()),
                role
        );

        Profile profile = new Profile(
                user,
                fullName,
                phone
        );
        user.setProfile(profile);

        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(savedUser);

        return new AuthResponse(jwtToken, UserResponse.fromEntity(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String loginEmail = request.getEmail() != null && !request.getEmail().trim().isEmpty()
                ? request.getEmail().trim()
                : (request.getUsername() != null ? request.getUsername().trim() : "");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginEmail,
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmailIgnoreCase(loginEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginEmail));

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken, UserResponse.fromEntity(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @org.springframework.transaction.annotation.Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(!user.isEnabled());
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @org.springframework.transaction.annotation.Transactional
    public UserResponse updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setRole(role);
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public UserResponse getUserByEmail(String email) {
        var userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            return UserResponse.fromEntity(userOpt.get());
        }
        var empOpt = employeeRepository.findByEmailIgnoreCase(email);
        if (empOpt.isPresent()) {
            return UserResponse.fromEmployee(empOpt.get());
        }
        throw new ResourceNotFoundException("User not found with email: " + email);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // 1. Fetch all farms owned by this user
        List<Farm> userFarms = farmRepository.findByOwnerId(id);
        if (userFarms != null && !userFarms.isEmpty()) {
            List<Long> farmIds = userFarms.stream().map(Farm::getId).toList();
            // Delete favorites referencing these farms
            favoriteRepository.deleteByFarmIdIn(farmIds);
            // Delete messages referencing these farms
            messageRepository.deleteByFarmIdIn(farmIds);
            // Delete the farms (cascades to images, sheds, distances)
            farmRepository.deleteAll(userFarms);
        }

        // 2. Delete messages sent or received by this user
        messageRepository.deleteByUserId(id);

        // 3. Delete favorites saved by this user
        favoriteRepository.deleteByUserId(id);

        // 4. Delete notifications for this user
        notificationRepository.deleteByUserId(id);

        // 5. Delete the user (cascades to profile)
        userRepository.delete(user);
    }

    @org.springframework.transaction.annotation.Transactional
    public void changePassword(String email, org.evabirds.evabirds.dto.ChangePasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @org.springframework.transaction.annotation.Transactional
    public AuthResponse updateEmail(String currentEmail, org.evabirds.evabirds.dto.UpdateEmailRequest request) {
        User user = userRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentEmail));

        String newEmail = request.getNewEmail() != null ? request.getNewEmail().trim().toLowerCase() : "";
        if (newEmail.isEmpty()) {
            throw new BadRequestException("New email address is required");
        }

        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new BadRequestException("New email address must be different from current email");
        }

        if (request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            throw new BadRequestException("Verification code is required");
        }

        // Verify OTP against the requested new email
        boolean otpValid = otpService.verifyRegistrationOtp(newEmail, request.getOtp().trim());
        if (!otpValid) {
            throw new BadRequestException("Invalid or expired verification code");
        }

        // Verify password only if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new IllegalArgumentException("Incorrect password confirmation");
            }
        }

        if (userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new UserAlreadyExistsException("Email is already registered: " + newEmail);
        }

        user.setEmail(newEmail);
        User updatedUser = userRepository.save(user);
        String newToken = jwtService.generateToken(updatedUser);

        return new AuthResponse(newToken, UserResponse.fromEntity(updatedUser));
    }
}