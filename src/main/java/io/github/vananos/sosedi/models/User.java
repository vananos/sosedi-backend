package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "SURNAME", nullable = false)
    private String surname;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "BIRTHDAY")
    private LocalDate birthday;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "INTERESTS")
    private String interests;

    @Column(name = "description")
    private String description;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.EMAIL_UNCONFIRMED;

    public enum UserStatus {
        EMAIL_UNCONFIRMED,
        EMAIL_CONFIRMED,
        PROFILE_FILLED
    }
}
