package com.glee.xjpa.util;

import cn.hutool.core.util.StrUtil;
import com.glee.xjpa.exception.XjpaInitException;
import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.impl.RepositoryPlugin;
import com.glee.xjpa.repository.impl.RepositoryController;
import com.glee.xjpa.table.EntityTable;
import com.glee.xjpa.table.TableFieldContainer;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.core.annotation.AnnotationUtils;

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

        if (table != null && StrUtil.isNotEmpty(table.name())) {
            return table.name();
        }

        if (StrUtil.isNotEmpty(entity.name())) {
            return entity.name();
        }

        return NameUtil.toUUCase(tableEntityType.getSimpleName());
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


    public static <K, T extends StandardJpaRepository> K getNextId(T repository){
        RepositoryController repositoryController = new RepositoryPlugin(repository);
        return repositoryController.getNextId();
    }

}
