package ru.otus.cachehw;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private final Map<K, V> storage = new WeakHashMap<>();
    private final List<WeakReference<HwListener<K, V>>> listeners = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void put(K key, V value) {
        boolean existed = storage.containsKey(key);
        storage.put(key, value);
        notifyListeners(key, value, existed ? "UPDATED" : "CREATED");
    }

    @Override
    public void remove(K key) {
        V old = storage.remove(key);
        if (old != null || storage.containsKey(key) == false) {
            notifyListeners(key, old, "REMOVED");
        }
    }

    @Override
    public V get(K key) {
        return storage.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        if (listener == null) return;
        listeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        if (listener == null) return;
        synchronized (listeners) {
            listeners.removeIf(ref -> {
                HwListener<K, V> l = ref.get();
                return l == null || l == listener;
            });
        }
    }

    private void notifyListeners(K key, V value, String action) {
        List<WeakReference<HwListener<K, V>>> toRemove = new ArrayList<>();
        synchronized (listeners) {
            for (WeakReference<HwListener<K, V>> ref : listeners) {
                HwListener<K, V> l = ref.get();
                if (l != null) {
                    try {
                        l.notify(key, value, action);
                    } catch (Exception ignored) {
                    }
                } else {
                    toRemove.add(ref);
                }
            }
            if (!toRemove.isEmpty()) {
                listeners.removeAll(toRemove);
            }
        }
    }
}
