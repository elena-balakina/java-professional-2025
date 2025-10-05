package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import ru.otus.model.Client;

public interface ClientDao {
    Optional<Client> findById(long id);

    List<Client> findAllOrderByIdDesc();

    Client create(String name, String street, String phone);
}
