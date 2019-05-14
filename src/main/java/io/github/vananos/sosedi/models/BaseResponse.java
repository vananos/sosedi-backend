package io.github.vananos.sosedi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseResponse {
    @JsonProperty("status")
    private ResultStatus status;

    @JsonProperty("errors")
    private Object errors;

    public enum ResultStatus {
        SUCCESS,
        FAIL
    }
}
