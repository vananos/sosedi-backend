package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATIONS")
@Data
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOTIFICATION_FREQUENCY")
    @Enumerated(EnumType.STRING)
    private NotificationFrequency notificationFrequency;

    @Column(name = "LAST_SENT", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastSentTime;
}