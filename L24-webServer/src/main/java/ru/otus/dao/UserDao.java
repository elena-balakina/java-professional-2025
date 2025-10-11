package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.model.User;

public interface UserDao {

    Optional<User> findById(long id);

    Optional<User> findRandomUser();

    Optional<User> findByLogin(String login);

    List<User> findAllOrderByIdDesc();

    User create(String name, String login, String password);
}
