package ru.otus.crm.model;

import java.util.ArrayList;
import java.util.List;

/** Utilities for creating detached deep copies for caching. */
public final class Copies {
    private Copies() {}

    public static Client copy(Client src) {
        if (src == null) return null;
        Client c = new Client();
        c.setId(src.getId());
        c.setName(src.getName());
        if (src.getAddress() != null) {
            Address a = new Address();
            a.setId(src.getAddress().getId());
            a.setStreet(src.getAddress().getStreet());
            c.setAddress(a);
        }
        if (src.getPhones() != null) {
            List<Phone> list = new ArrayList<>(src.getPhones().size());
            for (Phone p : src.getPhones()) {
                if (p == null) continue;
                Phone np = new Phone();
                np.setId(p.getId());
                np.setNumber(p.getNumber());
                list.add(np);
            }
            c.setPhones(list);
        }
        return c;
    }
}
