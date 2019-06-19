package io.github.vananos.sosedi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Data
public class GeoInfo {
    @JsonProperty("address")
    @NotNull
    private String address;

    @JsonProperty("latLng")
    @NotNull
    private Coordinates latLng;

    @Data
    public static class Coordinates {
        @JsonProperty("lat")
        @NotNull
        private Double lat;

        @JsonProperty("lng")
        @NotNull
        private Double lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoInfo geoInfo = (GeoInfo) o;
        return Objects.equals(latLng, geoInfo.latLng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latLng);
    }

}
