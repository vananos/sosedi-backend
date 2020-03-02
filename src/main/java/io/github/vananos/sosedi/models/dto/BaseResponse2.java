package io.github.vananos.sosedi.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class BaseResponse2<T> {
    @JsonProperty("errors")
    private List<Error> errors;

    @JsonProperty("data")
    private T data;
}