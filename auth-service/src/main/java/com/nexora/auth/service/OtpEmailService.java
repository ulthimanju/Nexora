package com.nexora.auth.service;

import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.exception.AuthException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static com.nexora.auth.constants.ErrorMessages.EMAIL_SEND_FAILED;
import static com.nexora.auth.constants.ServiceConstants.OTP_EMAIL_SUBJECT;
import static com.nexora.auth.constants.ServiceConstants.OTP_EMAIL_TEMPLATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendOtpEmail(String email, String purpose, String otp, Duration ttl) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(email);
            helper.setSubject(OTP_EMAIL_SUBJECT);
            helper.setText(String.format(OTP_EMAIL_TEMPLATE, purpose, otp, ttl.toMinutes()), true);
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail);
            }
            mailSender.send(message);
            log.info(LogMessages.OTP_EMAIL_SENT, email);
        } catch (MessagingException e) {
            throw new AuthException(EMAIL_SEND_FAILED, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
