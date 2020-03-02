package io.github.vananos.sosedi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class Error {
    @JsonProperty("id")
    private String id;

    @JsonProperty("description")
    private String description;
}