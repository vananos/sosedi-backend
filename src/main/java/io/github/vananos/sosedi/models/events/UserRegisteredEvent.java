package io.github.vananos.sosedi.models.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UserRegisteredEvent {
    @NonNull
    String username;
    @NonNull
    String email;
    @NonNull
    String pinCode;
    @NonNull
    String emailConfirmationId;
}