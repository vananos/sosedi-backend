package io.github.vananos.sosedi.models.dto.ad;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.Attitude;
import io.github.vananos.sosedi.models.RoomType;
import lombok.Data;

import java.util.List;

@Data
public class AdResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("isLandLord")
    private Boolean landLord;

    @JsonProperty("placeId")
    private Object placeId;

    @JsonProperty("female")
    private Boolean female;

    @JsonProperty("male")
    private Boolean male;

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

    @JsonProperty("addConv")
    private List<String> additionalConveniences;

    @JsonProperty("rentPay")
    private Integer rentPay;

    @JsonProperty("description")
    private String description;
}