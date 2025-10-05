package ru.otus.dao;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;

public class HibernateClientDao implements ClientDao {

    private final SessionFactory sf;

    public HibernateClientDao(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Optional<Client> findById(long id) {
        try (Session s = sf.openSession()) {
            var q = s.createQuery(
                    "select distinct c from Client c " + "left join fetch c.address "
                            + "left join fetch c.phones "
                            + "where c.id = :id",
                    Client.class);
            q.setParameter("id", id);
            var list = q.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        }
    }

    @Override
    public List<Client> findAllOrderByIdDesc() {
        try (Session s = sf.openSession()) {
            return s.createQuery(
                            "select distinct c from Client c " + "left join fetch c.address "
                                    + "left join fetch c.phones "
                                    + "order by c.id desc",
                            Client.class)
                    .getResultList();
        }
    }

    @Override
    public Client create(String name, String street, String phoneNumber) {
        try (Session s = sf.openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                var client = new Client();
                client.setName(name);

                var addr = new Address(street);
                client.setAddress(addr);

                if (phoneNumber != null && !phoneNumber.isBlank()) {
                    var phone = new Phone(phoneNumber.trim());
                    phone.setClient(client);
                    client.getPhones().add(phone);
                }

                s.persist(client);
                tx.commit();
                return client;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
