package homework;

import java.util.*;

public class CustomerReverseOrder {

    private final Deque<Customer> customers = new ArrayDeque<>();

    public void add(Customer customer) {
        customers.addLast(customer);
    }

    public Customer take() {
        return customers.pollLast();
    }
}
