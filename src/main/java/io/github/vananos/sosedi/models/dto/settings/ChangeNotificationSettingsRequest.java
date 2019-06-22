package io.github.vananos.sosedi.models.dto.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.NotificationFrequency;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangeNotificationSettingsRequest {

    @JsonProperty("userId")
    @NotNull
    private Long userId;

    @JsonProperty("freq")
    @NotNull
    private NotificationFrequency notificationFrequency;
}