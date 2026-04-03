package com.nexora.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import static com.nexora.auth.constants.ValidationMessages.*;

@Data
public class OtpVerificationRequest {

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID)
    private String email;

    @NotBlank(message = OTP_REQUIRED)
    @Pattern(regexp = "\\d{6}", message = OTP_LENGTH)
    private String otp;
}
