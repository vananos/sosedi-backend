package io.github.vananos.sosedi.models.dto.passwordreset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("password")
    private String password;
}