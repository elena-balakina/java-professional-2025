package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

    @Id
    @SequenceGenerator(name = "phone_seq", sequenceName = "phone_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_seq")
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Phone(String number) {
        this.number = number;
    }

    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    @Override
    public String toString() {
        return "Phone{id=" + id + ", number='" + number + "'}";
    }
}
