package org.evabirds.evabirds.service;

import org.evabirds.evabirds.entity.OtpVerification;
import org.evabirds.evabirds.exception.BadRequestException;
import org.evabirds.evabirds.exception.UserAlreadyExistsException;
import org.evabirds.evabirds.repository.OtpVerificationRepository;
import org.evabirds.evabirds.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private final SecureRandom secureRandom = new SecureRandom();

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OtpService(
            OtpVerificationRepository otpRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void sendRegistrationOtp(String rawEmail) {
        if (rawEmail == null || rawEmail.trim().isEmpty()) {
            throw new BadRequestException("Email address cannot be empty");
        }
        String email = rawEmail.trim().toLowerCase();

        // Check if user is already registered
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new UserAlreadyExistsException("An account is already registered with this email address: " + email);
        }

        // Rate limit / cooldown check (60 seconds)
        Optional<OtpVerification> latestOpt = otpRepository.findTopByEmailIgnoreCaseOrderByCreatedAtDesc(email);
        if (latestOpt.isPresent()) {
            OtpVerification latest = latestOpt.get();
            if (latest.getCreatedAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())) {
                throw new BadRequestException("Please wait " + RESEND_COOLDOWN_SECONDS + " seconds before requesting another code.");
            }
        }

        // Generate 6-digit code
        int codeInt = 100000 + secureRandom.nextInt(900000);
        String code = String.valueOf(codeInt);

        // Store OTP
        OtpVerification otpVerification = new OtpVerification(email, code, OTP_EXPIRY_MINUTES);
        otpRepository.save(otpVerification);

        // Send via Email (with dev fallback)
        emailService.sendOtpEmail(email, code);
    }

    @Transactional
    public boolean verifyRegistrationOtp(String rawEmail, String code) {
        if (rawEmail == null || code == null) {
            return false;
        }
        String email = rawEmail.trim().toLowerCase();

        Optional<OtpVerification> opt = otpRepository.findTopByEmailIgnoreCaseOrderByCreatedAtDesc(email);
        if (opt.isEmpty()) {
            throw new BadRequestException("No verification code requested for this email.");
        }

        OtpVerification record = opt.get();
        if (record.isExpired()) {
            throw new BadRequestException("Verification code has expired. Please request a new code.");
        }

        if (record.isVerified()) {
            return true; // Already verified
        }

        record.incrementAttempts();

        if (!record.isValid(code)) {
            otpRepository.save(record);
            if (record.getAttempts() >= 5) {
                throw new BadRequestException("Too many invalid attempts. Please request a new code.");
            }
            throw new BadRequestException("Invalid verification code. Please check and try again.");
        }

        record.setVerified(true);
        otpRepository.save(record);
        return true;
    }

    public boolean isEmailVerified(String rawEmail) {
        if (rawEmail == null) return false;
        String email = rawEmail.trim().toLowerCase();
        Optional<OtpVerification> opt = otpRepository.findTopByEmailIgnoreCaseOrderByCreatedAtDesc(email);
        return opt.map(v -> v.isVerified() && !v.isExpired()).orElse(false);
    }

    @Transactional
    public void sendEmailChangeOtp(String currentEmail, String rawNewEmail) {
        if (rawNewEmail == null || rawNewEmail.trim().isEmpty()) {
            throw new BadRequestException("New email address cannot be empty");
        }
        String newEmail = rawNewEmail.trim().toLowerCase();

        if (currentEmail != null && currentEmail.trim().equalsIgnoreCase(newEmail)) {
            throw new BadRequestException("New email address must be different from your current email.");
        }

        // Check if new email is already in use by another user
        if (userRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new UserAlreadyExistsException("An account is already registered with this email address: " + newEmail);
        }

        // Rate limit / cooldown check (60 seconds)
        Optional<OtpVerification> latestOpt = otpRepository.findTopByEmailIgnoreCaseOrderByCreatedAtDesc(newEmail);
        if (latestOpt.isPresent()) {
            OtpVerification latest = latestOpt.get();
            if (latest.getCreatedAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())) {
                throw new BadRequestException("Please wait " + RESEND_COOLDOWN_SECONDS + " seconds before requesting another code.");
            }
        }

        // Generate 6-digit code
        int codeInt = 100000 + secureRandom.nextInt(900000);
        String code = String.valueOf(codeInt);

        // Store OTP
        OtpVerification otpVerification = new OtpVerification(newEmail, code, OTP_EXPIRY_MINUTES);
        otpRepository.save(otpVerification);

        // Send via Email
        emailService.sendEmailChangeOtpEmail(newEmail, code);
    }
}
