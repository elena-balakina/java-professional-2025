package ru.otus.service;

import java.util.*;
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
        Optional<Client> client = clientRepository.findById(id);
        client.ifPresent(this::hydrate);
        return client;
    }

    public List<Client> findAllOrderByNewest() {
        List<Client> list = clientRepository.findAllByOrderByIdDesc();
        list.forEach(this::hydrate);
        return list;
    }

    @Transactional
    public Client createOrUpdate(Client client, String street, List<String> phoneNumbers) {
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
        Set<Phone> phones = new HashSet<>();
        for (String phoneNumber : phoneNumbers) {
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                phones.add(new Phone(null, phoneNumber.trim(), client.getId()));
            }
        }
        phoneRepository.saveAll(phones);
        client.setPhones(phones);
        client.setAddress(address);
        return client;
    }

    @Transactional
    public void deleteById(Long id) {
        phoneRepository.deleteAllByClientId(id);
        clientRepository.deleteById(id);
    }

    private void hydrate(Client client) {
        if (client.getAddressId() != null) {
            addressRepository.findById(client.getAddressId()).ifPresent(client::setAddress);
        }
        Set<Phone> phones = new HashSet<>();
        phoneRepository.findAllByClientId(client.getId()).forEach(phones::add);
        client.setPhones(phones);
    }
}
