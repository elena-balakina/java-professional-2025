package ru.otus.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.service.ClientService;

@SuppressWarnings({"removal", "unchecked"})
@WebMvcTest(
        controllers = ClientController.class,
        excludeAutoConfiguration = {
            org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
            org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class,
            org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration.class,
            org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class
        })
class ClientControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    org.springframework.validation.Validator validator;

    @MockBean
    ClientService clientService;

    private static Client client(long id, String name, String street, String... phoneNumbers) {
        Client client = new Client();
        client.setId(id);
        client.setName(name);

        if (street != null) {
            Address address = new Address();
            address.setId(100L);
            address.setStreet(street);
            client.setAddressId(address.getId());
            client.setAddress(address);
        }

        Set<Phone> phoneSet = (phoneNumbers == null)
                ? new java.util.LinkedHashSet<>()
                : java.util.Arrays.stream(phoneNumbers)
                        .map(p -> new Phone(null, p, id))
                        .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        client.setPhones(phoneSet);
        return client;
    }

    @Test
    @DisplayName("GET /clients — returns 'add client' form and clients list")
    void index_returnsViewAndModel() throws Exception {
        when(clientService.findAllOrderByNewest())
                .thenReturn(List.of(
                        client(2L, "John", "Baker 221B", "+44 20 1234 5678"),
                        client(1L, "Ivan", "Arbat 10", "+7 999 111-22-33")));

        mvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/index"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attributeExists("street"))
                .andExpect(model().attributeExists("phones"))
                .andExpect(model().attribute("phones", hasSize(2)))
                .andExpect(model().attributeExists("clients"))
                .andExpect(model().attribute("clients", iterableWithSize(2)));

        verify(clientService).findAllOrderByNewest();
    }

    @Test
    @DisplayName("POST /clients — successful client creation + redirect to /clients")
    void create_valid_redirects() throws Exception {
        when(clientService.createOrUpdate(any(Client.class), any(), anyList())).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(post("/clients")
                        .param("name", "New User")
                        .param("addressId", "1")
                        .param("street", "Nevsky, 1")
                        .param("phones", "+7 900 000-00-00")
                        .param("phones", "+7 901 111-11-11"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        ArgumentCaptor<Client> clientCap = ArgumentCaptor.forClass(Client.class);
        ArgumentCaptor<List<String>> phonesCap = ArgumentCaptor.forClass(List.class);

        verify(clientService).createOrUpdate(clientCap.capture(), eq("Nevsky, 1"), phonesCap.capture());
        Client saved = clientCap.getValue();
        List<String> numbers = phonesCap.getValue();

        assert saved.getId() == null;
        assert saved.getName().equals("New User");
        assert numbers.size() == 2;
        assert numbers.getFirst().contains("+7");
    }

    @Test
    @DisplayName("POST /clients — empty name → stay on clients/index")
    void create_invalid_showsForm() throws Exception {
        when(clientService.findAllOrderByNewest()).thenReturn(List.of()); // для повторного рендера

        mvc.perform(post("/clients").param("name", "").param("street", "Any"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/index"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("clients"))
                .andExpect(model().attributeExists("phones"))
                .andExpect(model().attributeExists("street"));

        verify(clientService, never()).createOrUpdate(any(), any(), anyList());
    }

    @Test
    @DisplayName("GET /clients/{id}/edit — the same page with filled in form")
    void editForm_populatesModel() throws Exception {
        Client c = client(10L, "Alice", "Main st.", "+1 111");
        when(clientService.findById(10L)).thenReturn(Optional.of(c));
        when(clientService.findAllOrderByNewest()).thenReturn(List.of(c));

        mvc.perform(get("/clients/10/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("clients/index"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attribute("street", "Main st."))
                .andExpect(model().attribute("phones", hasSize(1)))
                .andExpect(model().attributeExists("clients"));

        verify(clientService).findById(10L);
        verify(clientService).findAllOrderByNewest();
    }

    @Test
    @DisplayName("POST /clients/{id} — successful edit → redirect to /clients")
    void update_valid_redirects() throws Exception {
        when(clientService.createOrUpdate(any(Client.class), any(), anyList())).thenAnswer(inv -> inv.getArgument(0));

        mvc.perform(post("/clients/42")
                        .param("name", "Updated")
                        .param("street", "Lenina, 5")
                        .param("phones", "+7 902 222-22-22"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        ArgumentCaptor<Client> cap = ArgumentCaptor.forClass(Client.class);
        verify(clientService).createOrUpdate(cap.capture(), eq("Lenina, 5"), anyList());
        assert cap.getValue().getId().equals(42L);
        assert cap.getValue().getName().equals("Updated");
    }

    @Test
    @DisplayName("POST /clients/{id}/delete — delete and redirect to /clients")
    void delete_redirects() throws Exception {
        mvc.perform(post("/clients/5/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        verify(clientService).deleteById(5L);
    }
}
