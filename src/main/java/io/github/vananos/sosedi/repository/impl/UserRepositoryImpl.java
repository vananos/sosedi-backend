package io.github.vananos.sosedi.repository.impl;

import io.github.vananos.sosedi.models.User;
import io.github.vananos.sosedi.repository.UserRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static io.github.vananos.sosedi.models.User.SELECT_USER_BY_EMAIL;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("uncheked")
    public User getUserByEmail(String email) {
        return (User) sessionFactory.getCurrentSession()
                .getNamedQuery(SELECT_USER_BY_EMAIL)
                .setParameter("email", email)
                .uniqueResult();
    }

    @Override
    public void addUser(User user) {
        sessionFactory.getCurrentSession().save(user);
    }
}
