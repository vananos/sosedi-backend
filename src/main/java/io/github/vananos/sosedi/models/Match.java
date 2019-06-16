package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "MATCHES", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"FIRST_USER_ID", "SECOND_USER_ID"})
})
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRST_USER_ID")
    private User firstUser;

    @Column(name = "FIRST_USER_STATE")
    @Enumerated(EnumType.STRING)
    private MatchState firstUserState = MatchState.NEW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECOND_USER_ID")
    private User secondUser;

    @Column(name = "SECOND_USER_STATE")
    @Enumerated(EnumType.STRING)
    private MatchState secondUserState = MatchState.NEW;

    public enum MatchState {
        NEW, DECLINED, ACCEPTED
    }
}