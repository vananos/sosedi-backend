package io.github.vananos.sosedi.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class EmailConfirmationInfo {
    @NonNull
    String username;
    @NonNull
    String email;
    @NonNull
    String pinCode;
    @NonNull
    String emailConfirmationId;
}