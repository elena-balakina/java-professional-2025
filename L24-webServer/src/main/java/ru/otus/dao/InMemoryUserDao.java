package ru.otus.dao;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import ru.otus.model.User;

@SuppressWarnings("java:S2068")
public class InMemoryUserDao implements UserDao {

    public static final String DEFAULT_PASSWORD = "11111";
    private final SecureRandom random = new SecureRandom();
    private final Map<Long, User> users = new HashMap<>();

    private final AtomicLong idAutoInc = new AtomicLong(0L);

    public InMemoryUserDao() {
        // примеры начальных пользователей
        putInitial(new User(nextId(), "Крис Гир", "user1", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Ая Кэш", "user2", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Десмин Боргес", "user3", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Кетер Донохью", "user4", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Стивен Шнайдер", "user5", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Джанет Вэрни", "user6", DEFAULT_PASSWORD));
        putInitial(new User(nextId(), "Брэндон Смит", "user7", DEFAULT_PASSWORD));
    }

    private long nextId() {
        return idAutoInc.incrementAndGet();
    }

    private void putInitial(User u) {
        users.put(u.getId(), u);
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findRandomUser() {

        return users.values().stream().skip(random.nextInt(users.size() - 1)).findFirst();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(v -> v.getLogin().equals(login)).findFirst();
    }

    @Override
    public List<User> findAllOrderByIdDesc() {
        return users.values().stream()
                .sorted(Comparator.comparingLong(User::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public User create(String name, String login, String password) {
        long id = nextId();
        User u = new User(id, name, login, password);
        users.put(id, u);
        return u;
    }
}
