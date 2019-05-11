package io.github.vananos.sosedi.models.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.validation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {
    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 15;
    public static final int PASSWORD_MIN_LENGTH = 6;

    @JsonProperty("name")
    @NotNull
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    @JsonProperty("surname")
    @NotNull
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String surname;

    @JsonProperty("email")
    @NotNull
    @Email
    private String email;

    @JsonProperty("password")
    @ValidPassword
    private String password;

    @JsonProperty("passwordConfirmation")
    @ValidPassword
    private String passwordConfirmation;
}
