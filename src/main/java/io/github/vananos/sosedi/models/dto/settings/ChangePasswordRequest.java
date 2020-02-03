package io.github.vananos.sosedi.models.dto.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequest {

    @JsonProperty("userId")
    @NotNull
    private Long userId;

    @JsonProperty("pincode")
    @NotNull
    private String password;
}