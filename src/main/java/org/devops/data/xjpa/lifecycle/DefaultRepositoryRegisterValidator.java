package org.devops.data.xjpa.lifecycle;

import org.devops.data.xjpa.exception.XjpaInitException;
import org.devops.data.xjpa.util.EntityUtil;
import org.devops.data.xjpa.util.TableUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/11/16
 * @description 默认校验
 */
public class DefaultRepositoryRegisterValidator implements RepositoryRegisterValidator {

    private final Environment environment;

    public DefaultRepositoryRegisterValidator(Environment environment) {
        this.environment = environment;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void validate(Class<?> repositoryType) {
        Class entityType = (Class) TableUtil.getTableEntityType(repositoryType);

        Assert.isTrue(entityType.isAnnotationPresent(Entity.class), "@Entity annotation not found: " + entityType);
        Assert.isTrue(entityType.isAnnotationPresent(Table.class), "@Table annotation not found: " + entityType);

        List<Field> allField = EntityUtil.getFields(entityType);
        long idCount = allField.stream().filter(field -> field.isAnnotationPresent(Id.class)).count();

        // 实体必须包含一个@Id注释字段
        Assert.isTrue(idCount == 1, "entity must contains one @Id annotated field: " + entityType);

        Field keyField = allField.stream().filter(field -> field.isAnnotationPresent(Id.class)).findFirst().orElseThrow(() -> new XjpaInitException("keyField get error"));


        Class keyType = (Class) TableUtil.getTableKeyType(repositoryType);

        Assert.isAssignable(Serializable.class, keyType, "entity id must be Serializable type: " + entityType);
        Assert.isAssignable(keyField.getType(), keyType, "entity [" + entityType +  "]: " + entityType);

        Assert.isTrue(keyField.isAnnotationPresent(GeneratedValue.class), "entity key requires a @GeneratedValue annotation: " + entityType);


    }

}
