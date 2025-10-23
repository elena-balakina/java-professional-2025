package ru.otus.web;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.otus.model.Client;
import ru.otus.service.ClientService;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @InitBinder("client")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("phones");
    }

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return "clients/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("phones", List.of("", ""));
        model.addAttribute("street", "");
        return "clients/form";
    }

    @PostMapping
    public String create(
            @ModelAttribute("client") @Valid Client client,
            BindingResult br,
            @RequestParam(name = "street", required = false) String street,
            @RequestParam(name = "phones", required = false) List<String> phones) {
        if (br.hasErrors()) {
            return "clients/form";
        }
        clientService.createOrUpdate(client, street, phones == null ? List.of() : phones);
        return "redirect:/clients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Client c = clientService.findById(id).orElseThrow();
        model.addAttribute("client", c);
        model.addAttribute("street", c.getAddress() != null ? c.getAddress().getStreet() : "");
        List<String> nums = c.getPhones() != null && !c.getPhones().isEmpty()
                ? c.getPhones().stream().map(p -> p.getNumber()).collect(Collectors.toList())
                : List.of("");
        model.addAttribute("phones", nums);
        return "clients/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("client") @Valid Client client,
            BindingResult br,
            @RequestParam(name = "street", required = false) String street,
            @RequestParam(name = "phones", required = false) List<String> phones) {
        if (br.hasErrors()) {
            return "clients/form";
        }
        client.setId(id);
        clientService.createOrUpdate(client, street, phones == null ? List.of() : phones);
        return "redirect:/clients";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        clientService.deleteById(id);
        return "redirect:/clients";
    }
}
