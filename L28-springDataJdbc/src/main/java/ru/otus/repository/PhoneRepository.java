package ru.otus.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {
    Iterable<Phone> findAllByClientId(Long clientId);

    @Modifying
    @Query("DELETE FROM phone WHERE client_id = :clientId")
    void deleteAllByClientId(@Param("clientId") Long clientId);
}
