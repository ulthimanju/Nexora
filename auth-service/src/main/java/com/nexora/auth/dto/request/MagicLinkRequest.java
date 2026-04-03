package com.nexora.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static com.nexora.auth.constants.ValidationMessages.EMAIL_INVALID;
import static com.nexora.auth.constants.ValidationMessages.EMAIL_REQUIRED;

@Data
public class MagicLinkRequest {

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID)
    private String email;
}
