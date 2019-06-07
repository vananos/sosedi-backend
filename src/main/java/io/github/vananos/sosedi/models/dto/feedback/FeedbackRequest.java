package io.github.vananos.sosedi.models.dto.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FeedbackRequest {
    @JsonProperty("email")
    @NotNull
    @Email
    private String email;

    @JsonProperty("name")
    @NotNull
    @Size(min = 2, max = 15)
    private String name;

    @JsonProperty("message")
    @Size(max = 1024)
    private String message;
}
