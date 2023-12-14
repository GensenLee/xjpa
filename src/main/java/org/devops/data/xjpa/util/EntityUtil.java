package org.devops.data.xjpa.util;

import org.devops.core.utils.util.BeanUtil;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/8
 * @description 实体类工具
 */
public class EntityUtil {

    /**
     * @param entities
     * @param keyField
     * @param <K>
     * @param <V>
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static <K, V> Collection<K> readKeys(Collection<V> entities, Field keyField) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(entity -> (K) BeanUtil.getValue(entity, keyField.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    /**
     * 获取实体类的属性
     * @param entityType
     * @return
     */
    public static List<Field> getFields(Class<?> entityType) {
        List<Field> allField = BeanUtil.getAllField(entityType);
        Map<String, Field> result = new HashMap<>();
        for (Field field : allField) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!result.containsKey(field.getName()) || field.getDeclaringClass() == entityType) {
                result.put(field.getName(), field);
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(result.values()));
    }

}
