package ru.otus.model;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("client")
public class Client {
    @Id
    private Long id;

    @NotBlank
    private String name;

    @Column("address_id")
    private Long addressId;

    @Transient
    private Address address;

    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;
}
