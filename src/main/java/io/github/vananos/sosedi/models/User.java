package io.github.vananos.sosedi.models;

import lombok.Data;

@Data
public class User {
    private String name;
    private String surname;
    private String password;
    private String email;
}
