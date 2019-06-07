package io.github.vananos.sosedi.repository;

import io.github.vananos.sosedi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email")
    User getUserByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("update User set avatar = :avatar where id = :userId")
    void updateAvatarForUser(@Param("avatar") String avatar, @Param("userId") Long userId);

    Optional<User> findByEmailConfirmationId(String confirmationId);
}
