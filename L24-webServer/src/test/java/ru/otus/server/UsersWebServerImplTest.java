package ru.otus.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static ru.otus.server.utils.WebServerHelper.buildUrl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.dao.ClientDao;
import ru.otus.dao.UserDao;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.UserAuthService;

@DisplayName("Тест сервера должен ")
class UsersWebServerImplTest {

    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String API_USER_URL = "api/user";

    private static final long DEFAULT_ID = 1L;

    private static Gson gson;
    private static UsersWebServer webServer;
    private static HttpClient http;

    @BeforeAll
    static void setUp() throws Exception {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        http = HttpClient.newBuilder().cookieHandler(cm).build();
        gson = new GsonBuilder().serializeNulls().create();

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        UserDao userDao = mock(UserDao.class);
        ClientDao clientDao = mock(ClientDao.class);
        UserAuthService authService = mock(UserAuthService.class);

        Client testClient1 = new Client();
        testClient1.setId(2L);
        testClient1.setName("Alice");
        Address testAddress1 = new Address();
        testAddress1.setId(11L);
        testAddress1.setStreet("Street 1");
        testClient1.setAddress(testAddress1);
        testClient1.setPhones(List.of());

        Client testClient2 = new Client();
        testClient2.setId(1L);
        testClient2.setName("Bob");
        Address testAddress2 = new Address();
        testAddress2.setId(12L);
        testAddress2.setStreet("Street 2");
        testClient2.setAddress(testAddress2);
        Phone testPhone = new Phone();
        testPhone.setId(101L);
        testPhone.setNumber("111");
        testClient2.setPhones(List.of(testPhone));

        given(clientDao.findById(DEFAULT_ID)).willReturn(Optional.of(testClient2));
        given(clientDao.findAllOrderByIdDesc()).willReturn(List.of(testClient1, testClient2));

        given(templateProcessor.getPage(eq("users.html"), anyMap())).willAnswer(inv -> {
            @SuppressWarnings("unchecked")
            var params = (java.util.Map<String, Object>) inv.getArgument(1);
            @SuppressWarnings("unchecked")
            var clients = (List<Client>) params.get("clients");
            var sb = new StringBuilder("<html><body><table>");
            for (Client c : clients) {
                String street = c.getAddress() != null ? c.getAddress().getStreet() : "";
                String phone = (c.getPhones() != null && !c.getPhones().isEmpty())
                        ? c.getPhones().get(0).getNumber()
                        : "—";
                sb.append("<tr><td>")
                        .append(c.getId())
                        .append("</td><td>")
                        .append(c.getName())
                        .append("</td><td>")
                        .append(street)
                        .append("</td><td>")
                        .append(phone)
                        .append("</td></tr>");
            }
            sb.append("</table></body></html>");
            return sb.toString();
        });
        given(authService.authenticate(anyString(), anyString())).willReturn(true);

        webServer = new UsersWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, authService, userDao, gson, templateProcessor, clientDao);
        webServer.start();
        var loginReq = HttpRequest.newBuilder()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, "login")))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(BodyPublishers.ofString("login=test&password=pw"))
                .build();
        http.send(loginReq, HttpResponse.BodyHandlers.discarding());
    }

    @AfterAll
    static void tearDown() throws Exception {
        webServer.stop();
    }

    @DisplayName("возвращать корректные данные при запросе клиента по id")
    @Test
    void shouldReturnCorrectClientById() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_USER_URL, String.valueOf(DEFAULT_ID))))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        assertThat(json.get("id").getAsLong()).isEqualTo(DEFAULT_ID);
        assertThat(json.get("name").getAsString()).isEqualTo("Bob");
        assertThat(json.getAsJsonObject("address").get("street").getAsString()).isEqualTo("Street 2");
        assertThat(json.getAsJsonArray("phones")
                        .get(0)
                        .getAsJsonObject()
                        .get("number")
                        .getAsString())
                .isEqualTo("111");
    }

    @DisplayName("возвращать список клиентов на странице /users (новые сверху)")
    @Test
    void shouldReturnClientsOnUsersPageWhenAuthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, "users")))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        String body = response.body();
        assertThat(body).contains("Alice");
        assertThat(body).contains("Bob");
        assertThat(body).contains("Street 1");
        assertThat(body).contains("Street 2");
        assertThat(body).contains("111");
        assertThat(body.indexOf("Alice")).isLessThan(body.indexOf("Bob"));
    }
}
