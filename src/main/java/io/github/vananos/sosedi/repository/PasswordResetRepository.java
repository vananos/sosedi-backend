package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.PasswordResetTask;
import io.github.vananos.sosedi.models.PasswordResetTask.PasswordResetTaskStatus;
import io.github.vananos.sosedi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetTask, Long> {

    PasswordResetTask findBySecret(String secret);

    List<PasswordResetTask> findByTargetUserAndStatus(User user, PasswordResetTaskStatus passwordResetTaskStatus);
}