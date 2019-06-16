package io.github.vananos.sosedi.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "MATCH_PROCESSOR_QUEUE")
public class MatchProcessorTask {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "USER_ID")
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.NEW;

    @Column(name = "CREATION_TIME", columnDefinition = "TIMESTAMP")
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "START_PROCESSING_TIME", columnDefinition = "TIMESTAMP")
    private LocalDateTime startProcessing;

    @Column(name = "END_PROCESSING_TIME", columnDefinition = "TIMESTAMP")
    private LocalDateTime endProcessing;

    public enum TaskStatus {
        NEW, PROCESSING, PROCESSED, SKIPPED
    }
}