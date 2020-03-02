package io.github.vananos.sosedi.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true, builderClassName = "Builder")
public class RegistrationInfo {
    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private String email;
}