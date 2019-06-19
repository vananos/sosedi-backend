package io.github.vananos.sosedi.models.dto.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.dto.ad.AdResponse;
import io.github.vananos.sosedi.models.dto.userprofile.UserProfileInfo;
import lombok.Data;

@Data
public class MatchResponseEntity {

    @JsonProperty("matchId")
    private Long matchId;

    @JsonProperty("userInfo")
    private UserProfileInfo userProfileInfo;

    @JsonProperty("adInfo")
    private AdResponse adInfo;
}