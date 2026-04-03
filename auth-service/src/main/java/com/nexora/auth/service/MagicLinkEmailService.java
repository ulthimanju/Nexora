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

import static com.nexora.auth.constants.ErrorMessages.EMAIL_SEND_FAILED;
import static com.nexora.auth.constants.ServiceConstants.MAGIC_LINK_EMAIL_SUBJECT;

@Service
@RequiredArgsConstructor
@Slf4j
public class MagicLinkEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendMagicLinkEmail(String email, String magicLinkUrl) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(email);
            helper.setSubject(MAGIC_LINK_EMAIL_SUBJECT);
            helper.setText(buildHtmlTemplate(magicLinkUrl), true);
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail);
            }
            mailSender.send(message);
            log.info(LogMessages.MAGIC_LINK_SENT, email);
        } catch (MessagingException e) {
            throw new AuthException(EMAIL_SEND_FAILED, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String buildHtmlTemplate(String magicLinkUrl) {
        return """
                <html>
                <body style="margin:0;padding:0;font-family:'Helvetica Neue',Arial,sans-serif;background:#f8f6f4;">
                  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#f8f6f4;padding:24px 0;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 12px 30px rgba(0,0,0,0.06);">
                          <tr>
                            <td style="background:#c96442;padding:24px 32px;color:#ffffff;font-size:20px;font-weight:700;letter-spacing:0.4px;">
                              Nexora
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:32px 32px 12px 32px;color:#1f1f1f;font-size:18px;font-weight:600;">
                              Sign in with your magic link
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:0 32px 24px 32px;color:#4a4a4a;font-size:15px;line-height:1.6;">
                              Tap the button below to securely sign in. This link works once and expires in 15 minutes.
                            </td>
                          </tr>
                          <tr>
                            <td align="center" style="padding:8px 32px 32px 32px;">
                              <a href=\"""" + magicLinkUrl + """\" style="display:inline-block;background:#c96442;color:#ffffff;text-decoration:none;padding:14px 28px;border-radius:8px;font-weight:700;font-size:16px;letter-spacing:0.2px;">Access your account</a>
                            </td>
                          </tr>
                          <tr>
                            <td style="padding:0 32px 32px 32px;color:#6a6a6a;font-size:13px;line-height:1.6;">
                              If you did not request this, you can safely ignore this email. For your security, the link will stop working after it is used once or after it expires.
                            </td>
                          </tr>
                          <tr>
                            <td style="background:#f1efed;padding:16px 32px;color:#8a8a8a;font-size:12px;text-align:center;">
                              © Nexora. Seamless learning, secure access.
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """;
    }
}
