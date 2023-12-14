package org.devops.data.xjpa.util;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
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
                .map(entity -> (K) BeanUtil.getFieldValue(entity, keyField.getName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    /**
     * 获取实体类的属性
     *
     * @param entityType
     * @return
     */
    public static List<Field> getFields(Class<?> entityType) {
        BeanDesc beanDesc = BeanUtil.getBeanDesc(entityType);
        Map<String, Field> result = new HashMap<>();
        for (PropDesc propDesc : beanDesc.getProps()) {
            if (Modifier.isStatic(propDesc.getField().getModifiers())) {
                continue;
            }
            if (!result.containsKey(propDesc.getFieldName()) || propDesc.getField().getDeclaringClass() == entityType) {
                result.put(propDesc.getFieldName(), propDesc.getField());
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(result.values()));
    }

}
