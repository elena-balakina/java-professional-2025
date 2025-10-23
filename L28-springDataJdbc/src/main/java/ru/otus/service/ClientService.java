package ru.otus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.AddressRepository;
import ru.otus.repository.ClientRepository;
import ru.otus.repository.PhoneRepository;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final PhoneRepository phoneRepository;

    public ClientService(
            ClientRepository clientRepository, AddressRepository addressRepository, PhoneRepository phoneRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
    }

    public Iterable<Client> findAll() {
        Iterable<Client> clients = clientRepository.findAll();
        clients.forEach(this::hydrate);
        return clients;
    }

    public Optional<Client> findById(Long id) {
        Optional<Client> c = clientRepository.findById(id);
        c.ifPresent(this::hydrate);
        return c;
    }

    public List<Client> findAllOrderByNewest() {
        return clientRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Client createOrUpdate(Client client, String street, List<String> numbers) {
        Address address = null;
        if (client.getAddressId() != null) {
            address = addressRepository.findById(client.getAddressId()).orElse(null);
        }
        if (address == null) address = new Address(null, street);
        else address.setStreet(street);
        address = addressRepository.save(address);
        client.setAddressId(address.getId());

        client = clientRepository.save(client);

        phoneRepository.deleteAllByClientId(client.getId());
        List<Phone> phones = new ArrayList<>();
        for (String n : numbers) {
            if (n != null && !n.isBlank()) {
                phones.add(new Phone(null, n.trim(), client.getId()));
            }
        }
        phones.forEach(phoneRepository::save);
        client.setPhones(phones);
        client.setAddress(address);
        return client;
    }

    @Transactional
    public void deleteById(Long id) {
        phoneRepository.deleteAllByClientId(id);
        clientRepository.deleteById(id);
    }

    private void hydrate(Client c) {
        if (c.getAddressId() != null) {
            addressRepository.findById(c.getAddressId()).ifPresent(c::setAddress);
        }
        List<Phone> phones = new ArrayList<>();
        phoneRepository.findAllByClientId(c.getId()).forEach(phones::add);
        c.setPhones(phones);
    }
}
