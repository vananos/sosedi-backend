package io.github.vananos.sosedi.models.dto.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.components.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequest {

    @JsonProperty("userId")
    @NotNull
    private Long userId;

    @JsonProperty("password")
    @NotNull
    @ValidPassword
    private String password;
}