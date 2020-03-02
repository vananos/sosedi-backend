package io.github.vananos.sosedi.models.dto.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Builder(toBuilder = true, builderClassName = "Builder")
public class RegistrationRequest {
    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 15;

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
}
