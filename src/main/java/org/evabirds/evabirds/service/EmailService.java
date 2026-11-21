package org.evabirds.evabirds.service;

import jakarta.mail.internet.MimeMessage;
import org.evabirds.evabirds.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:srikantha.l598@gmail.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otpCode) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new BadRequestException("Recipient email address cannot be empty.");
        }

        String target = toEmail.trim();
        String sender = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail.trim() : "srikantha.l598@gmail.com";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender, "Evabirds Poultry Rentals");
            helper.setTo(target);
            helper.setSubject("Your Evabirds Verification Code: " + otpCode);

            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 24px; color: #1e293b; }
                    .card { max-width: 480px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
                    .header { background: #059669; padding: 24px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 22px; font-weight: 700; letter-spacing: -0.5px; }
                    .content { padding: 32px 24px; text-align: center; }
                    .title { font-size: 18px; font-weight: 600; margin-bottom: 8px; color: #0f172a; }
                    .subtitle { font-size: 14px; color: #64748b; margin-bottom: 24px; line-height: 1.5; }
                    .otp-box { background: #f0fdf4; border: 2px dashed #059669; border-radius: 8px; padding: 18px; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #065f46; display: inline-block; margin-bottom: 24px; }
                    .footer { padding: 16px 24px; background: #f8fafc; border-top: 1px solid #e2e8f0; font-size: 12px; color: #94a3b8; text-align: center; line-height: 1.4; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>Eva<span style="color: #a7f3d0;">birds</span></h1>
                    </div>
                    <div class="content">
                      <div class="title">Verify Your Email Address</div>
                      <div class="subtitle">Use the verification code below to complete your registration on Evabirds:</div>
                      <div class="otp-box">%s</div>
                      <div class="subtitle" style="font-size: 13px; margin-bottom: 0;">This code is valid for <strong>5 minutes</strong>. For security, never share this code with anyone.</div>
                    </div>
                    <div class="footer">
                      &copy; 2026 Evabirds &bull; An EigenVega Corporate Product<br>
                      If you did not initiate this request, you can safely ignore this email.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(otpCode);

            helper.setText(htmlBody, true);
            mailSender.send(message);
            logger.info("[EVABIRDS OTP] Successfully dispatched OTP email to: {}", target);

        } catch (Exception ex) {
            logger.error("[EVABIRDS OTP] Failed to dispatch email via SMTP to {}: {}", target, ex.getMessage(), ex);
            throw new BadRequestException("Unable to send verification email: " + (ex.getMessage() != null ? ex.getMessage() : "SMTP error"));
        }
    }

    public void sendEmailChangeOtpEmail(String toEmail, String otpCode) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new BadRequestException("Recipient email address cannot be empty.");
        }

        String target = toEmail.trim();
        String sender = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail.trim() : "srikantha.l598@gmail.com";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender, "Evabirds Poultry Rentals");
            helper.setTo(target);
            helper.setSubject("Evabirds Email Change Verification Code: " + otpCode);

            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 24px; color: #1e293b; }
                    .card { max-width: 480px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
                    .header { background: #059669; padding: 24px; text-align: center; }
                    .header h1 { color: #ffffff; margin: 0; font-size: 22px; font-weight: 700; letter-spacing: -0.5px; }
                    .content { padding: 32px 24px; text-align: center; }
                    .title { font-size: 18px; font-weight: 600; margin-bottom: 8px; color: #0f172a; }
                    .subtitle { font-size: 14px; color: #64748b; margin-bottom: 24px; line-height: 1.5; }
                    .otp-box { background: #f0fdf4; border: 2px dashed #059669; border-radius: 8px; padding: 18px; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #065f46; display: inline-block; margin-bottom: 24px; }
                    .footer { padding: 16px 24px; background: #f8fafc; border-top: 1px solid #e2e8f0; font-size: 12px; color: #94a3b8; text-align: center; line-height: 1.4; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <h1>Eva<span style="color: #a7f3d0;">birds</span></h1>
                    </div>
                    <div class="content">
                      <div class="title">Verify Your New Email Address</div>
                      <div class="subtitle">Use the verification code below to confirm and update your registered account email on Evabirds:</div>
                      <div class="otp-box">%s</div>
                      <div class="subtitle" style="font-size: 13px; margin-bottom: 0;">This code is valid for <strong>5 minutes</strong>. If you did not request this email change, please secure your account immediately.</div>
                    </div>
                    <div class="footer">
                      &copy; 2026 Evabirds &bull; An EigenVega Corporate Product<br>
                      If you did not initiate this change, please ignore this email.
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(otpCode);

            helper.setText(htmlBody, true);
            mailSender.send(message);
            logger.info("[EVABIRDS OTP] Successfully dispatched email change OTP to: {}", target);

        } catch (Exception ex) {
            logger.error("[EVABIRDS OTP] Failed to dispatch email change OTP via SMTP to {}: {}", target, ex.getMessage(), ex);
            throw new BadRequestException("Unable to send verification email: " + (ex.getMessage() != null ? ex.getMessage() : "SMTP error"));
        }
    }
}
