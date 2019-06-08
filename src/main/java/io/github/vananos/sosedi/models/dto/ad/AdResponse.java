package io.github.vananos.sosedi.models.dto.ad;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AdResponse {

    @JsonProperty("isLandLord")
    private Boolean landLord;

    @JsonProperty("placeId")
    private Object placeId;

    @JsonProperty("female")
    private Boolean female;

    @JsonProperty("male")
    private Boolean male;

    @JsonProperty("smoking")
    private String smoking;

    @JsonProperty("animals")
    private String animals;

    @JsonProperty("minAge")
    private Integer minAge;

    @JsonProperty("maxAge")
    private Integer maxAge;

    @JsonProperty("roomType")
    private List<String> roomType;

    @JsonProperty("addConv")
    private List<String> additionalConveniences;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("description")
    private String description;
}