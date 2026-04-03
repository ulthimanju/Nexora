// Refactored: extracted 2 constants
package com.nexora.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import static com.nexora.auth.constants.ValidationMessages.*;

@Data
public class LoginRequest {

    @NotBlank(message = USERNAME_OR_EMAIL_REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = PASSWORD_REQUIRED)
    private String password;
}
