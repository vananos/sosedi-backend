package io.github.vananos.sosedi.models.dto.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.NotificationFrequency;
import lombok.Data;

@Data
public class UserSettingsResponse {
    @JsonProperty("freq")
    private NotificationFrequency notificationFrequency;
}
