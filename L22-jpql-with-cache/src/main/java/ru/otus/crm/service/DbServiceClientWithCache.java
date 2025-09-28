package ru.otus.crm.service;

import static ru.otus.crm.model.Copies.copy;

import java.util.List;
import java.util.Optional;
import ru.otus.cachehw.HwCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Copies;

public class DbServiceClientWithCache implements DBServiceClient {

    private final DataTemplate<Client> dataTemplate;
    private final TransactionManager tx;
    private final HwCache<String, Client> cache;

    public DbServiceClientWithCache(
            TransactionManager tx, DataTemplate<Client> dataTemplate, HwCache<String, Client> cache) {
        this.tx = tx;
        this.dataTemplate = dataTemplate;
        this.cache = cache;
    }

    private String key(long id) {
        return "Client:" + id;
    }

    @Override
    public Client saveClient(Client client) {
        return tx.doInTransaction(session -> {
            if (client.getId() == null) {
                var id = dataTemplate.insert(session, client);
                client.setId(id.getId());
            } else {
                dataTemplate.update(session, client);
            }
            cache.put(key(client.getId()), copy(client));
            return client;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        var k = key(id);
        var cached = cache.get(k);
        if (cached != null) {
            return Optional.of(copy(cached));
        }
        return tx.doInReadOnlyTransaction(session -> dataTemplate.findById(session, id))
                .map(db -> {
                    cache.put(k, copy(db));
                    return copy(db);
                });
    }

    @Override
    public List<Client> findAll() {
        var list = tx.doInReadOnlyTransaction(dataTemplate::findAll);
        for (var c : list) {
            cache.put(key(c.getId()), copy(c));
        }
        return list.stream().map(Copies::copy).toList();
    }
}
