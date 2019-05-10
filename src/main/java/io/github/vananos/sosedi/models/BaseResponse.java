package io.github.vananos.sosedi.models;

import lombok.Data;

@Data
public class BaseResponse {
    private ResultStatus result;
    private Object errors;

    public enum ResultStatus {
        SUCCESS,
        FAIL
    }
}
