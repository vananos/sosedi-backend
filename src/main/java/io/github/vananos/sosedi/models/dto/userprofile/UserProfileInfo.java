package io.github.vananos.sosedi.models.dto.userprofile;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.vananos.sosedi.models.Gender;
import io.github.vananos.sosedi.models.Interests;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "\\+\\d \\(\\d{3}\\) \\d{3} \\d{2} \\d{2}")
    private String phone;

    @JsonProperty("interests")
    @NotNull
    private List<Interests> interests;

    @JsonProperty("description")
    @Size(max = 512)
    @NotNull
    private String description;

    @JsonProperty("isNewUser")
    private Boolean isNewUser;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("gender")
    @NotNull
    private Gender gender;
}
