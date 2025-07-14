package homework;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CustomerService {

    private final NavigableMap<Customer, String> customers = new TreeMap<>(Comparator.comparing(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> entry = customers.firstEntry();
        return copyEntry(entry);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> entry = customers.higherEntry(customer);
        return copyEntry(entry);
    }

    public void add(Customer customer, String data) {
        customers.put(customer, data);
    }

    private Map.Entry<Customer, String> copyEntry(Map.Entry<Customer, String> entry) {
        if (entry == null) {
            return null;
        }
        Customer original = entry.getKey();
        Customer copy = new Customer(original.getId(), original.getName(), original.getScores());
        return Map.entry(copy, entry.getValue());
    }
}
