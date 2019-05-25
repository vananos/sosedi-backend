package io.github.vananos.sosedi.models.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileInfo {
    @JsonProperty("userId")
    @NotNull
    private Long id;

    @JsonProperty("name")
    @Length(min = 2, max = 20)
    @NotNull
    private String name;

    @JsonProperty("surname")
    @Length(min = 2, max = 20)
    @NotNull
    private String surname;

    @JsonProperty("birthday")
    @NotNull
    private LocalDate birthday;

    @JsonProperty("phone")
    @NotNull
    private String phone;

    @JsonProperty("interests")
    @NotNull
    private List<String> interests;

    @JsonProperty("description")
    @Size(max = 512)
    @NotNull
    private String description;

    @JsonProperty("isNewUser")
    private Boolean isNewUser;

}
