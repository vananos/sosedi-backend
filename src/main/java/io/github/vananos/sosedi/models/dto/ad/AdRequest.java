package io.github.vananos.sosedi.models.dto.ad;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.Attitude;
import io.github.vananos.sosedi.models.Convenience;
import io.github.vananos.sosedi.models.RoomType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AdRequest {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("male")
    @NotNull
    private Boolean male;

    @JsonProperty("female")
    @NotNull
    private Boolean female;

    @JsonProperty("landlord")
    @NotNull
    private Boolean landlord;

    @JsonProperty("placeId")
    @NotNull
    private String placeId;

    @JsonProperty("smoking")
    @NotNull
    private Attitude smoking;

    @JsonProperty("animals")
    @NotNull
    private Attitude animals;

    @JsonProperty("minAge")
    @NotNull
    private Integer minAge;

    @JsonProperty("maxAge")
    @NotNull
    private Integer maxAge;

    @JsonProperty("roomType")
    @NotNull
    private List<RoomType> roomType;

    @JsonProperty("conveniences")
    @NotNull
    private List<Convenience> conveniences;

    @JsonProperty("rentPay")
    @NotNull
    private Integer rentPay;

    @JsonProperty("description")
    @NotNull
    @Size(max = 512)
    private String description;
}