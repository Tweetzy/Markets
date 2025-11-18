package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.annotations.*;
import ca.tweetzy.flight.database.repository.EntityMapper;
import ca.tweetzy.flight.utils.SerializeUtil;
import ca.tweetzy.markets.api.market.core.MarketType;
import ca.tweetzy.markets.api.market.layout.Layout;
import ca.tweetzy.markets.impl.MarketLayout;
import ca.tweetzy.markets.impl.PlayerMarket;
import ca.tweetzy.markets.impl.ServerMarket;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Custom EntityMapper for Markets that handles special serialization:
 * - ItemStack via SerializeUtil
 * - List<String> via ";;;" delimiter
 * - Layout via getJSONString()/decodeJSON()
 * - List<UUID> via comma-separated
 */
public class MarketsEntityMapper<T> implements EntityMapper<T> {
    
    private final Class<T> entityClass;
    private final Map<String, Field> columnToField = new HashMap<>();
    private final Map<Field, String> fieldToColumn = new HashMap<>();
    private Field idField;
    private String idColumnName;
    
    public MarketsEntityMapper(@NotNull Class<T> entityClass) {
        this.entityClass = entityClass;
        analyzeEntity();
    }
    
    private void analyzeEntity() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            
            field.setAccessible(true);
            
            String columnName;
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.value().isEmpty()) {
                columnName = columnAnnotation.value();
            } else {
                columnName = toSnakeCase(field.getName());
            }
            
            columnToField.put(columnName, field);
            fieldToColumn.put(field, columnName);
            
            if (field.isAnnotationPresent(Id.class)) {
                idField = field;
                idColumnName = columnName;
            }
        }
        
        if (idField == null) {
            throw new IllegalArgumentException("Entity class " + entityClass.getName() + " must have a field annotated with @Id");
        }
    }
    
    @Override
    @Nullable
    public T map(@NotNull ResultSet resultSet) throws SQLException {
        try {
            // Handle polymorphic Market types
            T entity;
            if (PlayerMarket.class.isAssignableFrom(entityClass)) {
                // Check if it's a ServerMarket
                String typeStr = null;
                try {
                    if (hasColumn(resultSet, "type")) {
                        typeStr = resultSet.getString("type");
                    }
                } catch (SQLException ignored) {}
                
                if (typeStr != null && MarketType.valueOf(typeStr.toUpperCase()) == MarketType.SERVER) {
                    @SuppressWarnings("unchecked")
                    Class<T> serverMarketClass = (Class<T>) ServerMarket.class;
                    entity = serverMarketClass.getDeclaredConstructor().newInstance();
                } else {
                    entity = entityClass.getDeclaredConstructor().newInstance();
                }
            } else {
                entity = entityClass.getDeclaredConstructor().newInstance();
            }
            
            for (Map.Entry<String, Field> entry : columnToField.entrySet()) {
                String columnName = entry.getKey();
                Field field = entry.getValue();
                
                if (!hasColumn(resultSet, columnName)) {
                    continue;
                }
                
                Object value = getValueFromResultSet(resultSet, columnName, field);
                if (value != null) {
                    field.set(entity, value);
                }
            }
            
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map entity from ResultSet", e);
        }
    }
    
    @Override
    @NotNull
    public Map<String, Object> toMap(@NotNull T entity) {
        Map<String, Object> map = new HashMap<>();
        
        for (Map.Entry<Field, String> entry : fieldToColumn.entrySet()) {
            Field field = entry.getKey();
            String columnName = entry.getValue();
            
            try {
                Object value = field.get(entity);
                
                // Handle special serialization
                if (value instanceof ItemStack) {
                    // Special case: MarketCategory.icon is stored as Material name, not serialized ItemStack
                    if (columnName.equals("icon") && entityClass.getSimpleName().equals("MarketCategory")) {
                        value = ((ItemStack) value).getType().name();
                    } else {
                        value = SerializeUtil.encodeItem((ItemStack) value);
                    }
                } else if (value instanceof Layout) {
                    value = ((Layout) value).getJSONString();
                } else if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    if (!list.isEmpty()) {
                        Object first = list.get(0);
                        if (first instanceof String) {
                            // List<String> - use ";;;" delimiter
                            value = String.join(";;;", (List<String>) list);
                        } else if (first instanceof UUID) {
                            // List<UUID> - use comma-separated
                            value = list.stream()
                                    .map(UUID.class::cast)
                                    .map(UUID::toString)
                                    .reduce((a, b) -> a + "," + b)
                                    .orElse("");
                        } else {
                            // Other lists - use JSON
                            value = new com.google.gson.Gson().toJson(value);
                        }
                    } else {
                        value = null;
                    }
                } else if (value instanceof UUID) {
                    value = value.toString();
                } else if (value instanceof Enum) {
                    value = ((Enum<?>) value).name();
                }
                
                map.put(columnName, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + field.getName(), e);
            }
        }
        
        return map;
    }
    
    @Override
    @NotNull
    public Object getId(@NotNull T entity) {
        try {
            Object id = idField.get(entity);
            if (id instanceof UUID) {
                return id.toString();
            }
            return id;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get ID from entity", e);
        }
    }
    
    @Override
    @NotNull
    public String getIdColumn() {
        return idColumnName;
    }
    
    @Nullable
    private Object getValueFromResultSet(@NotNull ResultSet rs, @NotNull String columnName, @NotNull Field field) throws SQLException {
        Class<?> fieldType = field.getType();
        Object value = rs.getObject(columnName);
        
        if (value == null || rs.wasNull()) {
            return null;
        }
        
        // Handle ItemStack
        if (fieldType == ItemStack.class) {
            if (value instanceof String) {
                // Special case: MarketCategory.icon is stored as Material name
                if (columnName.equals("icon") && field.getDeclaringClass().getSimpleName().equals("MarketCategory")) {
                    try {
                        return CompMaterial.matchCompMaterial((String) value).orElse(CompMaterial.CHEST).parseItem();
                    } catch (Exception e) {
                        return CompMaterial.CHEST.parseItem();
                    }
                } else {
                    try {
                        return SerializeUtil.decodeItem((String) value);
                    } catch (Exception e) {
                        return CompMaterial.AIR.parseItem();
                    }
                }
            }
        }
        
        // Handle Layout
        if (Layout.class.isAssignableFrom(fieldType)) {
            if (value instanceof String) {
                try {
                    return MarketLayout.decodeJSON((String) value);
                } catch (Exception e) {
                    return new ca.tweetzy.markets.impl.layout.HomeLayout();
                }
            }
        }
        
        // Handle List<String>
        if (fieldType == List.class && field.isAnnotationPresent(Nested.class)) {
            if (value instanceof String) {
                String str = (String) value;
                if (str.isEmpty()) {
                    return new ArrayList<>();
                }
                // Check if it's ";;;" delimited (List<String>) or comma-separated (List<UUID>)
                if (str.contains(";;;")) {
                    return new ArrayList<>(Arrays.asList(str.split(";;;")));
                } else if (str.contains(",") && !str.contains(";;;")) {
                    // Try to parse as UUIDs
                    try {
                        List<UUID> uuids = new ArrayList<>();
                        for (String uuidStr : str.split(",")) {
                            if (!uuidStr.trim().isEmpty()) {
                                uuids.add(UUID.fromString(uuidStr.trim()));
                            }
                        }
                        return uuids;
                    } catch (IllegalArgumentException e) {
                        // Not UUIDs, return as single string list
                        return new ArrayList<>(Collections.singletonList(str));
                    }
                } else {
                    return new ArrayList<>(Collections.singletonList(str));
                }
            }
        }
        
        // Handle UUID
        if (fieldType == UUID.class) {
            if (value instanceof String) {
                try {
                    return UUID.fromString((String) value);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        
        // Handle enums
        if (fieldType.isEnum()) {
            if (value instanceof String) {
                try {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Class<? extends Enum> enumClass = (Class<? extends Enum>) fieldType;
                    return Enum.valueOf(enumClass, ((String) value).toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        
        // Handle primitives
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            }
            if (value instanceof Boolean) {
                return value;
            }
        }
        
        // Type conversion for numbers
        if (fieldType == int.class || fieldType == Integer.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        
        if (fieldType == long.class || fieldType == Long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (value instanceof String) {
                try {
                    return Long.parseLong((String) value);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            }
        }
        
        if (fieldType == double.class || fieldType == Double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value instanceof String) {
                try {
                    return Double.parseDouble((String) value);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }
        
        return value;
    }
    
    private boolean hasColumn(@NotNull ResultSet rs, @NotNull String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    @NotNull
    private String toSnakeCase(@NotNull String camelCase) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}

