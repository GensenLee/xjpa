package org.devops.data.xjpa.util;

import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.DefaultRepositoryController;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryController;
import org.devops.data.xjpa.sql.executor.AbstractSqlExecutor;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description SqlExecutor util
 */
public class SqlExecutorUtil {


    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Result execute(AbstractSqlExecutor sqlExecutor, AbstractQueryRequest queryRequest) throws SQLException {
        if (sqlExecutor == null || queryRequest == null) {
            return Result.Builder.build(0);
        }
        return sqlExecutor.execute(queryRequest);
    }

    /**
     * @param sql
     * @param repository 用于获取执行的数据源
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public static List<Map<String, Object>> query(String sql, StandardJpaRepository repository) {
        RepositoryController controller = new DefaultRepositoryController(repository);

        RepositoryContext context = controller.getContext();

        ExecuteSession executeSession = context.localSessionManager();
        try {
            PreparedStatement preparedStatement = executeSession.readStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Map<String, Object>> result = new ArrayList<>();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    if (row.containsKey(columnLabel)) {
                        row.put(createAlias(columnLabel, row.keySet()), value);
                    } else {
                        row.put(columnLabel, value);
                    }
                }
                result.add(row);
            }
            return result;
        } catch (SQLException e) {
            throw new XjpaExecuteException(e);
        } finally {
            context.dispose();
        }
    }

    private static String createAlias(String columnName, Set<String> keySet) {
        if (!keySet.contains(columnName)) {
            return columnName;
        }

        return createAlias(columnName + "_1", keySet);
    }

}
