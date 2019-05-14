package io.github.vananos.sosedi.models.dto.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseResponse {
    @JsonProperty("status")
    private ResultStatus status;

    @JsonProperty("errors")
    private List<Error> errors;

    public enum ResultStatus {
        SUCCESS,
        FAIL
    }
}
