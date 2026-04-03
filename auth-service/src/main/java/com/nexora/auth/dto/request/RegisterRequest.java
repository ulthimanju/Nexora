// Refactored: extracted 6 constants
package com.nexora.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import static com.nexora.auth.constants.ValidationMessages.*;

@Data
public class RegisterRequest {

    @NotBlank(message = USERNAME_REQUIRED)
    @Size(min = 3, max = 50, message = USERNAME_SIZE)
    private String username;

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 8, message = PASSWORD_LENGTH)
    private String password;

    @NotBlank(message = ROLE_REQUIRED)
    private String role;
}
