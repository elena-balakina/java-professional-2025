package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientWithCache;

public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///
        // use Db Service Client With Cache
        HwCache<String, Client> cache = new MyCache<>();
        cache.addListener((k, v, a) -> log.info("cache event: {} -> {}", a, k));
        var dbServiceClient = new DbServiceClientWithCache(transactionManager, clientTemplate, cache);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        // create/update client
        var client = dbServiceClient.saveClient(new Client("dbServiceSecond"));

        // First read client from DB (without cache)
        long t1 = System.nanoTime();
        var selectedClient1 = dbServiceClient
                .getClient(client.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + client.getId()));
        long t2 = System.nanoTime();
        log.info("First read time (from DB): {} µs, client: {}", (t2 - t1) / 1000, selectedClient1);

        // Update client: cache should receive UPDATED
        dbServiceClient.saveClient(new Client(selectedClient1.getId(), "dbServiceSecondUpdated"));

        // Second read client from DB (with cache, should be quicker)
        long t3 = System.nanoTime();
        var selectedClient2 = dbServiceClient
                .getClient(selectedClient1.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + selectedClient1.getId()));
        long t4 = System.nanoTime();
        log.info("Second read time (from cache): {} µs, client: {}", (t4 - t3) / 1000, selectedClient2);

        // Read client again from cache
        long t5 = System.nanoTime();
        var selectedClient3 = dbServiceClient
                .getClient(selectedClient2.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + selectedClient2.getId()));
        long t6 = System.nanoTime();
        log.info("Third read time (from cache): {} µs, client: {}", (t6 - t5) / 1000, selectedClient3);
    }
}
