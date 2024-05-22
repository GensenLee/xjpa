package org.devops.data.xjpa.sql.executor.key;

import cn.hutool.core.bean.BeanUtil;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.sql.executor.EntityWrapper;
import org.devops.data.xjpa.table.EntityTableField;
import org.devops.data.xjpa.table.identifier.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/18
 * @description 借助context控制主键
 */
public class ContextEntityPrimaryKeyHandler<V> implements EntityPrimaryKeyHandler {
    protected static final Logger logger = LoggerFactory.getLogger(ContextEntityPrimaryKeyHandler.class);

    protected final Map<Integer, EntityWrapper<V>> entityWrappers;

    protected final EntityTableField primaryKeyField;

    private final boolean autoincrement;

    /**
     * 已经设置主键的下标
     */
    private int cursorIndex = 0;

    /**
     * 类型转换
     */
    private final Method typeCastMethod;

    private final RepositoryContext<?, V> context;

    /**
     * @param entities
     * @param context
     */
    public ContextEntityPrimaryKeyHandler(Collection<V> entities, RepositoryContext<?, V> context) {
        this.entityWrappers = wrap(entities);
        this.context = context;
        this.primaryKeyField = context.getEntityTable().getPrimaryKeyField();
        this.autoincrement = context.getEntityTable().isAutoincrement();
        this.typeCastMethod = findTypeCastMethod(primaryKeyField);
    }


    @Override
    public boolean writeGeneratedKeys(final IdentifierGenerator identifierGenerator) {
        if (identifierGenerator == null || primaryKeyField == null) {
            return false;
        }

        Field field = primaryKeyField.getJavaField();
        for (EntityWrapper<V> value : entityWrappers.values()){
            if (BeanUtil.getFieldValue(value.getEntity(), field.getName()) != null) {
                continue;
            }
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(value.getEntity().getClass(), field.getName());
            if (propertyDescriptor == null) {
                continue;
            }

            Method writeMethod = propertyDescriptor.getWriteMethod();
            try {
                writeMethod.invoke(value.getEntity(), identifierGenerator.next());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new XjpaExecuteException(e);
            }
        }

        return true;
    }


    /**
     * @param primaryKeyField
     * @return
     */
    private Method findTypeCastMethod(EntityTableField primaryKeyField) {
        if (primaryKeyField == null) {
            return null;
        }

        try {
            if (primaryKeyField.getJavaField().getType() == String.class) {
                return null;
            }

            return primaryKeyField.getJavaField().getType().getMethod("valueOf", String.class);
        } catch (NoSuchMethodException e) {
            logger.error("find typeCastMethod error", e);
        }
        return null;
    }

    /**
     * @param entities
     * @return
     */
    private Map<Integer, EntityWrapper<V>> wrap(Collection<V> entities) {
        int index = 1;
        Map<Integer, EntityWrapper<V>> result = new HashMap<>();
        for (V entity : entities) {
            result.put(index, new EntityWrapper<>(entity, index));
            index++;
        }
        return result;
    }

    /**
     * @param val
     * @return
     */
    private Object castKeyType(Object val) {
        Field javaField = primaryKeyField.getJavaField();

        if (javaField.getType() == String.class) {
            return val;
        }
        try {
            return typeCastMethod.invoke(javaField.getType(), String.valueOf(val));
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("cast key type error", e);
        }
        return null;
    }

    /**
     * @param key
     * @return 是否设置成功
     */
    private boolean setPrimaryKey(Object key) {
        if (primaryKeyField == null || cursorIndex > entityWrappers.size()) {
            return false;
        }
        Field javaField = primaryKeyField.getJavaField();
        javaField.setAccessible(true);
        EntityWrapper<V> entityWrapper = entityWrappers.get(cursorIndex);
        if (entityWrapper == null) {
            logger.error("setPrimaryKey error index={}, key={}", cursorIndex, key);
            return false;
        }
        try {
            if (javaField.get(entityWrapper.getEntity()) != null) {
                // 原有值时不需要set
                ++cursorIndex;
                return setPrimaryKey(key);
            }
            Object castTypeValue = castKeyType(key);
            javaField.set(entityWrapper.getEntity(), castTypeValue);
        } catch (IllegalAccessException e) {
            logger.error("set key error", e);
        } finally {
            javaField.setAccessible(false);
        }
        return true;
    }


    /**
     * @param generatedKeys
     */
    @Override
    public boolean writeGeneratedKeys(ResultSet generatedKeys) {
        if (!autoincrement || primaryKeyField == null) {
            // 无主键或非自增主键都不需要读取
            return false;
        }
        try {
            while (generatedKeys.next()) {
                ++cursorIndex;
                Object val = generatedKeys.getObject(1);
                setPrimaryKey(val);
            }
        } catch (SQLException e) {
            logger.error("writeGeneratedKeys error", e);
            return false;
        }
        return true;
    }

}
