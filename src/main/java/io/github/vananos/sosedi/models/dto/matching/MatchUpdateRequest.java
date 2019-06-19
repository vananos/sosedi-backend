package io.github.vananos.sosedi.models.dto.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MatchUpdateRequest {

    @JsonProperty("userId")
    @NotNull
    private Long userId;

    @JsonProperty("matchId")
    @NotNull
    private Long matchId;

    @JsonProperty("updateAction")
    @NotNull
    private MatchUpdateAction matchUpdateAction;

    public enum MatchUpdateAction {
        ACCEPT, DECLINE
    }
}
