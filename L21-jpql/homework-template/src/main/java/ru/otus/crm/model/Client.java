package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_seq", sequenceName = "client_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Phone> phones = new ArrayList<>();

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(String name) {
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = new ArrayList<>();
        if (phones != null) {
            // защитное копирование: не мутируем объекты из аргумента
            for (Phone p : phones) {
                addPhoneCopy(p);
            }
        }
    }

    public void addPhoneCopy(Phone phone) {
        attachPhone(new Phone(phone.getId(), phone.getNumber(), this));
    }

    public void addPhoneNumber(String number) {
        attachPhone(new Phone(null, number, this));
    }

    private void attachPhone(Phone phone) {
        this.phones.add(phone);
        phone.setClient(this);
    }

    public void removePhone(Phone phone) {
        this.phones.remove(phone);
        phone.setClient(null);
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        var copy = new Client(this.id, this.name);
        copy.address = (this.address == null) ? null : new Address(this.address.getId(), this.address.getStreet());
        this.phones.forEach(copy::addPhoneCopy);
        return copy;
    }

    @Override
    public String toString() {
        return "Client{id=" + id + ", name='" + name + "'}";
    }
}
