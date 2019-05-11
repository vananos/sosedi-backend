package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;

import static io.github.vananos.sosedi.models.User.SELECT_USER_BY_EMAIL;

@Data
@Entity
@Table(name = "USER")
@NamedQueries({
        @NamedQuery(name = SELECT_USER_BY_EMAIL, query = "select u from User u where u.email = :email")
})
public class User {
    public static final String SELECT_USER_BY_EMAIL = "SelectUserByEmail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "PASSWORD")
    private String password;
}
