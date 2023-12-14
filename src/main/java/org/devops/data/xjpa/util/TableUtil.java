package org.devops.data.xjpa.util;

import org.devops.data.xjpa.exception.XjpaInitException;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.table.EntityTable;
import org.devops.data.xjpa.table.TableFieldContainer;
import org.devops.core.utils.util.StringUtil;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Type;

/**
 * @author GENSEN
 * @date 2022/11/5
 * @description table
 */
public class TableUtil {

    private static final ParameterizedTypeUtil parameterizedTypeUtil = new ParameterizedTypeUtil(StandardJpaRepository.class);


    /**
     * @param repositoryType
     * @return
     */
    public static String getTableNameByRepositoryType(Class<?> repositoryType) {
        Class<?> tableEntityType = (Class<?>) getTableEntityType(repositoryType);

        return getTableNameByEntityType(tableEntityType);
    }

    /**
     * @param tableEntityType
     * @return
     */
    public static String getTableNameByEntityType(Class<?> tableEntityType) {
        Table table = AnnotationUtils.findAnnotation(tableEntityType, Table.class);
        Entity entity = AnnotationUtils.findAnnotation(tableEntityType, Entity.class);
        if (entity == null) {
            throw new XjpaInitException("entity:" + tableEntityType + " missing Entity annotation");
        }

        if (table != null && StringUtil.isNotEmpty(table.name())) {
            return table.name();
        }

        if (StringUtil.isNotEmpty(entity.name())) {
            return entity.name();
        }

        return StringUtil.toUUCase(tableEntityType.getSimpleName());
    }


    /**
     * @param repositoryType
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static EntityTable wrapEntityTable(Class<?> repositoryType, TableFieldContainer tableFieldContainer) {
        if (!StandardJpaRepository.class.isAssignableFrom(repositoryType)) {
            throw new XjpaInitException("error repositoryType : " + repositoryType);
        }

        Type keyType = parameterizedTypeUtil.getParameterizedType(repositoryType, 0);
        Type entityType = parameterizedTypeUtil.getParameterizedType(repositoryType, 1);

        String tableName = TableUtil.getTableNameByRepositoryType(repositoryType);

        return new EntityTable(tableName, (Class) entityType, (Class) keyType, tableFieldContainer);
    }

    /**
     * 获取table键类型
     *
     * @param repository
     * @return
     */
    public static Type getTableKeyType(Class<?> repository) {
        return parameterizedTypeUtil.getParameterizedType(repository, 0);
    }

    /**
     * 获取table实体类类型
     *
     * @param repository
     * @return
     */
    public static Type getTableEntityType(Class<?> repository) {
        return parameterizedTypeUtil.getParameterizedType(repository, 1);
    }

}
