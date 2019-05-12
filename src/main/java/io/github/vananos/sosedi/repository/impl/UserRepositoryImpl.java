package io.github.vananos.sosedi.repository.impl;

import io.github.vananos.sosedi.exceptions.UserAlreadyExists;
import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static io.github.vananos.sosedi.models.User.SELECT_USER_BY_EMAIL;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return entityManager.createNamedQuery(SELECT_USER_BY_EMAIL, User.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public void addUser(User user) {
        try {
            entityManager.persist(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExists();
        }
    }
}
