package io.github.vananos.sosedi.models.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class NewPinCodeRequestedEvent {
    @NonNull
    String email;
    @NonNull
    String username;
    @NonNull
    String pinCode;
}
