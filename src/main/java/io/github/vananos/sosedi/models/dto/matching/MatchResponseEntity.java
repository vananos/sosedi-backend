package io.github.vananos.sosedi.models.dto.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MatchResponseEntity {

    @JsonProperty("matchId")
    private Long matchId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("placeId")
    private String placeId;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("selfDescription")
    private String selfDescription;

    @JsonProperty("matchedUserId")
    private Long matchedUserId;
}