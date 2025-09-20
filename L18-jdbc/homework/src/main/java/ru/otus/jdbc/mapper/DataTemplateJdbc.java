package ru.otus.jdbc.mapper;

import static ru.otus.jdbc.utils.Utils.toSnake;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> classMeta;

    @Deprecated
    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.classMeta = null;
    }

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> classMeta) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.classMeta = classMeta;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), this::mapOne);
    }

    @Override
    public List<T> findAll(Connection connection) {
        try {
            return dbExecutor
                    .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), List.of(), this::mapList)
                    .orElse(List.of());
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public long insert(Connection connection, T entity) {
        var params = getFieldValues(getMeta().getFieldsWithoutId(), entity);
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        var params = new ArrayList<>(getFieldValues(getMeta().getFieldsWithoutId(), entity));
        params.add(getFieldValue(getMeta().getIdField(), entity));
        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T mapOne(ResultSet rs) {
        var list = mapList(rs);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<T> mapList(ResultSet rs) {
        List<T> out = new ArrayList<>();
        try {
            Constructor<T> ctor = getMeta().getConstructor();
            while (rs.next()) {
                T obj = ctor.newInstance();
                for (Field f : getMeta().getAllFields()) {
                    String col = toSnake(f.getName());
                    Object val = rs.getObject(col);
                    if (val != null) {
                        f.setAccessible(true);
                        f.set(obj, val);
                    }
                }
                out.add(obj);
            }
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
        return out;
    }

    private static List<Object> getFieldValues(List<Field> fields, Object entity) {
        List<Object> params = new ArrayList<>(fields.size());
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                params.add(f.get(entity));
            } catch (IllegalAccessException e) {
                throw new DataTemplateException(e);
            }
        }
        return params;
    }

    private static Object getFieldValue(Field f, Object entity) {
        try {
            f.setAccessible(true);
            return f.get(entity);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    private EntityClassMetaData<T> getMeta() {
        if (classMeta == null) throw new IllegalStateException("EntityClassMetaData required");
        return classMeta;
    }
}
