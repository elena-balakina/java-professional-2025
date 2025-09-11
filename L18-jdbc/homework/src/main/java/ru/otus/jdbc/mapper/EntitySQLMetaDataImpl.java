package ru.otus.jdbc.mapper;

import static ru.otus.jdbc.utils.Utils.toSnake;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import ru.otus.jdbc.utils.Utils;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> meta;
    private final String tableName;
    private final String idColumn;
    private final List<String> columnsWithoutId;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> meta) {
        this.meta = meta;
        this.tableName = toSnake(meta.getName());
        this.idColumn = toSnake(meta.getIdField().getName());
        this.columnsWithoutId = meta.getFieldsWithoutId().stream()
                .map(Field::getName)
                .map(Utils::toSnake)
                .toList();
    }

    @Override
    public String getSelectAllSql() {
        return "select * from " + tableName;
    }

    @Override
    public String getSelectByIdSql() {
        return "select * from " + tableName + " where " + idColumn + " = ?";
    }

    @Override
    public String getInsertSql() {
        String columns = String.join(", ", columnsWithoutId);
        String query = columnsWithoutId.stream().map(c -> "?").collect(Collectors.joining(", "));
        return "insert into " + tableName + " (" + columns + ") values (" + query + ")";
    }

    @Override
    public String getUpdateSql() {
        String set = columnsWithoutId.stream().map(c -> c + " = ?").collect(Collectors.joining(", "));
        return "update " + tableName + " set " + set + " where " + idColumn + " = ?";
    }
}
