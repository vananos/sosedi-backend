package io.github.vananos.sosedi.models.dto.ad;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.*;
import lombok.Data;

import java.util.List;

@Data
public class AdResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("landlord")
    private Boolean landLord;

    @JsonProperty("placeId")
    private GeoInfo placeId;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("smoking")
    private Attitude smoking;

    @JsonProperty("animals")
    private Attitude animals;

    @JsonProperty("minAge")
    private Integer minAge;

    @JsonProperty("maxAge")
    private Integer maxAge;

    @JsonProperty("roomType")
    private List<RoomType> roomType;

    @JsonProperty("conveniences")
    private List<Convenience> conveniences;

    @JsonProperty("rentPay")
    private Integer rentPay;

    @JsonProperty("description")
    private String description;
}