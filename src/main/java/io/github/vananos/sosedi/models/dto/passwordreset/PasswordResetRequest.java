package io.github.vananos.sosedi.models.dto.passwordreset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PasswordResetRequest {

    @JsonProperty("secret")
    @NotNull
    private String secret;

    @JsonProperty("pincode")
    private String password;
}