package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import ru.otus.crm.annotation.Id;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> entityClass;
    private final String name;
    private final Constructor<T> constructor;
    private final List<Field> allFields;
    private final Field idField;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.name = entityClass.getSimpleName();
        try {
            this.constructor = entityClass.getDeclaredConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Entity must have no-args constructor: " + entityClass, e);
        }
        this.allFields = collectAllFields(entityClass);
        this.idField = allFields.stream()
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("@Id field not found in " + entityClass));
        this.idField.setAccessible(true);
        this.fieldsWithoutId = allFields.stream()
                .filter(f -> !f.isAnnotationPresent(Id.class))
                .peek(f -> f.setAccessible(true))
                .toList();
    }

    private static List<Field> collectAllFields(Class<?> clazz) {
        List<Field> res = new ArrayList<>();
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true);
                res.add(f);
            }
        }
        return Collections.unmodifiableList(res);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
