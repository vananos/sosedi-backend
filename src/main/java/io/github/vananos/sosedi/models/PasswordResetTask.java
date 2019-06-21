package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "PASSWORD_RESET_QUEUE")
public class PasswordResetTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User targetUser;

    @Column(name = "SECRET")
    private String secret;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private PasswordResetTaskStatus status;

    public enum PasswordResetTaskStatus {
        NEW, RESET, CANCELLED
    }
}