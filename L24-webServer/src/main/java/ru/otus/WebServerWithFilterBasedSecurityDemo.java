package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.dao.ClientDao;
import ru.otus.dao.HibernateClientDao;
import ru.otus.dao.InMemoryUserDao;
import ru.otus.dao.UserDao;
import ru.otus.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerWithFilterBasedSecurity;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;
import ru.otus.utils.HibernateUtils;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница логина
    http://localhost:8080/login

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    // получить список клиентов
    GET http://localhost:8080/api/users

    // создать клиента
    POST http://localhost:8080/api/user
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        UserDao userDao = new InMemoryUserDao();

        var hibernateCfg = new Configuration().configure("hibernate.cfg.xml");
        var dbUrl = hibernateCfg.getProperty("hibernate.connection.url");
        var dbUser = hibernateCfg.getProperty("hibernate.connection.username");
        var dbPwd = hibernateCfg.getProperty("hibernate.connection.password");
        new MigrationsExecutorFlyway(dbUrl, dbUser, dbPwd).executeMigrationsWithCleanupDb();

        var sessionFactory = HibernateUtils.buildSessionFactory(hibernateCfg, Client.class, Address.class, Phone.class);

        ClientDao clientDao = new HibernateClientDao(sessionFactory);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(userDao);

        UsersWebServer usersWebServer = new UsersWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, authService, userDao, gson, templateProcessor, clientDao);

        usersWebServer.start();
        usersWebServer.join();
    }
}
