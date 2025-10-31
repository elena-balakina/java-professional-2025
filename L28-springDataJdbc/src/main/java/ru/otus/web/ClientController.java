package ru.otus.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private static final String ATTR_CLIENT = "client";
    private static final String ATTR_PHONES = "phones";
    private static final String ATTR_STREET = "street";
    private static final String ATTR_CLIENTS = "clients";

    private static final String VIEW_INDEX = "clients/index";
    private static final String REDIRECT_CLIENTS = "redirect:/clients";

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @InitBinder(ATTR_CLIENT)
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(ATTR_PHONES);
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute(ATTR_CLIENT, new Client());
        model.addAttribute(ATTR_STREET, "");
        model.addAttribute(ATTR_PHONES, List.of("", ""));
        model.addAttribute(ATTR_CLIENTS, clientService.findAllOrderByNewest());
        return VIEW_INDEX;
    }

    @PostMapping
    public String create(
            @ModelAttribute("client") @Valid Client client,
            BindingResult br,
            @RequestParam(name = ATTR_STREET, required = false) String street,
            @RequestParam(name = ATTR_PHONES, required = false) List<String> phones,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute(ATTR_CLIENTS, clientService.findAllOrderByNewest());
            model.addAttribute(ATTR_STREET, street == null ? "" : street);
            model.addAttribute(ATTR_PHONES, (phones == null || phones.isEmpty()) ? List.of("", "") : phones);
            return VIEW_INDEX;
        }
        clientService.createOrUpdate(client, street, phones == null ? List.of() : phones);
        return REDIRECT_CLIENTS;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client c = clientService.findById(id).orElseThrow();
        model.addAttribute(ATTR_CLIENT, c);
        model.addAttribute(ATTR_STREET, c.getAddress() != null ? c.getAddress().getStreet() : "");
        List<String> nums = (c.getPhones() == null || c.getPhones().isEmpty())
                ? List.of("")
                : c.getPhones().stream().map(Phone::getNumber).toList();
        model.addAttribute(ATTR_PHONES, nums);
        model.addAttribute(ATTR_CLIENTS, clientService.findAllOrderByNewest());
        return VIEW_INDEX;
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("client") @Valid Client client,
            BindingResult br,
            @RequestParam(name = ATTR_STREET, required = false) String street,
            @RequestParam(name = ATTR_PHONES, required = false) List<String> phones,
            Model model) {
        if (br.hasErrors()) {
            model.addAttribute(ATTR_CLIENTS, clientService.findAllOrderByNewest());
            model.addAttribute(ATTR_STREET, street == null ? "" : street);
            model.addAttribute(ATTR_PHONES, (phones == null || phones.isEmpty()) ? List.of("", "") : phones);
            return VIEW_INDEX;
        }
        client.setId(id);
        clientService.createOrUpdate(client, street, phones == null ? List.of() : phones);
        return REDIRECT_CLIENTS;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientService.deleteById(id);
        return REDIRECT_CLIENTS;
    }
}
