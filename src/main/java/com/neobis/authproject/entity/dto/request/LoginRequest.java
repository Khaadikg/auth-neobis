package com.neobis.authproject.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String username;
    @Length(min = 8, max = 15, message = "Password must be between 8 and 15 digits")
    private String password;
}
