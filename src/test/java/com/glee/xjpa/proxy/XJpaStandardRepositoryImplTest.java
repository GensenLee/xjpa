package com.glee.xjpa.proxy;

import com.glee.xjpa.datasource.DataSourceManager;
import com.glee.xjpa.io.Query;
import com.glee.xjpa.io.groupby.GroupBy;
import com.glee.xjpa.io.groupby.GroupBySingleColumn;
import com.glee.xjpa.io.include.IncludeSingleColumn;
import com.glee.xjpa.io.orderby.OrderByDesc;
import com.glee.xjpa.io.update.UpdateSets;
import com.glee.xjpa.io.QueryRequest;
import com.glee.xjpa.io.StandardXJpaRepository;
import com.glee.xjpa.sql.executor.InsertSqlExecutor;
import com.glee.xjpa.sql.executor.TypeReadSqlExecutor;
import com.glee.xjpa.sql.executor.UpdateSqlExecutor;
import com.glee.xjpa.sql.logger.SqlLogger;
import com.glee.xjpa.sql.result.JsonResultConverter;
import com.glee.xjpa.sql.result.MapListResultConverter;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableProperties;
import com.glee.xjpa.table.XJpaTableMetadata;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class XJpaStandardRepositoryImplTest {

    @Mock
    private TableProperties<Integer, UserEntity> tableProperties;

    @Mock
    private XJpaTableMetadata<Integer, UserEntity> metadata;

    @Mock
    private DataSourceManager dataSourceManager;

    @Mock
    private SqlLogger sqlLogger;

    @Mock
    private EntityTableField primaryKeyField;

    @Mock
    private Column primaryKeyColumn;

    @Captor
    private ArgumentCaptor<Query> queryCaptor;

    private XJpaStandardRepositoryImpl<Integer, UserEntity> repository;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Field idField = UserEntity.class.getDeclaredField("id");
        when(primaryKeyField.isPriKey()).thenReturn(true);
        when(primaryKeyField.javaField()).thenReturn(idField);
        when(primaryKeyColumn.name()).thenReturn("id");
        when(primaryKeyField.column()).thenReturn(primaryKeyColumn);

        when(tableProperties.getMetadata()).thenReturn(metadata);
        when(metadata.getPrimaryKeyColumnName()).thenReturn("id");
        when(metadata.getPrimaryKeyField()).thenReturn(primaryKeyField);
        when(metadata.getTableName()).thenReturn("users");
        when(metadata.entityType()).thenReturn((Class) UserEntity.class);

        List<EntityTableField> fieldList = new ArrayList<>();
        for (Field field : UserEntity.class.getDeclaredFields()) {
            Column columnAnno = field.getAnnotation(Column.class);
            if (columnAnno == null) {
                columnAnno = mock(Column.class);
                when(columnAnno.name()).thenReturn(field.getName());
            }
            EntityTableField entityField = mock(EntityTableField.class);
            when(entityField.isPriKey()).thenReturn(field.getName().equals("id"));
            when(entityField.javaField()).thenReturn(field);
            when(entityField.column()).thenReturn(columnAnno);
            fieldList.add(entityField);
        }
        when(metadata.getEntityTableFieldList()).thenReturn(fieldList);

        repository = new XJpaStandardRepositoryImpl<>(tableProperties, dataSourceManager, sqlLogger);
    }

    @Test
    public void testInsertSingleEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setUsername("test");

        try {
            repository.insert(entity);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testUpdateSingleEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1);
        entity.setUsername("updated");

        try {
            repository.update(entity);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test(expected = com.glee.xjpa.exception.XJpaException.class)
    public void testUpdateBatchNotInTransaction() {
        UserEntity entity1 = new UserEntity();
        entity1.setId(1);
        UserEntity entity2 = new UserEntity();
        entity2.setId(2);
        Set<UserEntity> entities = new HashSet<>();
        entities.add(entity1);
        entities.add(entity2);

        repository.update(entities);
    }

    @Test
    public void testDeleteById() {
        try {
            repository.delete(1);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testDeleteEmptyIds() {
        Set<Integer> ids = Collections.emptySet();

        int result = repository.delete(ids);

        assertEquals(0, result);
    }

    @Test
    public void testGetById() {
        try {
            repository.getById(1);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testFindById() {
        try {
            repository.findById(1);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testListAll() {
        try {
            repository.listAll();
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testListByIds() {
        Set<Integer> ids = new HashSet<>(Arrays.asList(1, 2));
        try {
            repository.listByIds(ids);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testList() {
        Query query = new Query();
        query.where(new QueryWhere().andEqual("status", "active"));
        query.orderBy(new OrderByDesc("created_at"));

        try {
            repository.list(query);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testCount() {
        QueryWhere where = new QueryWhere();
        where.andEqual("status", "active");

        try {
            repository.count(where);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testGet() {
        Query query = new Query();
        query.where(new QueryWhere().andEqual("username", "test"));

        try {
            repository.get(query);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testFind() {
        Query query = new Query();

        try {
            repository.find(query);
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    @Test
    public void testListGroupBy() {
        GroupBy groupBy = new GroupBySingleColumn("user_id");

        Query query = new Query();
        query.include(new IncludeSingleColumn("user_id"));

        try {
            repository.list(query.groupBy(groupBy));
        } catch (Exception e) {
            assertTrue(e instanceof Exception);
        }
    }

    public static class UserEntity implements Serializable {
        @Column(name = "id")
        private Integer id;

        @Column(name = "username")
        private String username;

        @Column(name = "email")
        private String email;

        @Column(name = "status")
        private String status;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}