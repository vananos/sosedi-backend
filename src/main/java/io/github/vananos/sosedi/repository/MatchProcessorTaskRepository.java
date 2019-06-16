package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.MatchProcessorTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchProcessorTaskRepository extends JpaRepository<MatchProcessorTask, Long> {
}