package io.github.vananos.sosedi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class BaseResponse<T> {

    @JsonProperty("errors")
    private List<Error> errors;

    @JsonProperty("data")
    private T data;
}
