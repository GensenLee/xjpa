package org.devops.data.xjpa.util;

import cn.hutool.core.date.StopWatch;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.DefaultRepositoryController;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.RepositoryController;
import org.devops.data.xjpa.sql.executor.AbstractSqlExecutor;
import org.devops.data.xjpa.sql.executor.result.reader.Result;
import org.devops.data.xjpa.sql.executor.query.AbstractQueryRequest;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author GENSEN
 * @date 2022/11/3
 * @description SqlExecutor util
 */
public class SqlExecutorUtil {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutorUtil.class);

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

        StopWatch stopWatch = new StopWatch("SqlExecutorUtil#query");
        stopWatch.start("executeSession.readStatement(sql)");
        try (PreparedStatement preparedStatement = executeSession.readStatement(sql)){
            stopWatch.stop();
            stopWatch.start("preparedStatement.executeQuery()");
            ResultSet resultSet = preparedStatement.executeQuery();
            stopWatch.stop();
            stopWatch.start("read resultSet");
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
            resultSet.close();
            stopWatch.stop();
            return result;
        } catch (SQLException e) {
            throw new XjpaExecuteException(e);
        } finally {
            context.dispose();
            logger.trace("query time use\n" + stopWatch.prettyPrint());
        }
    }

    private static String createAlias(String columnName, Set<String> keySet) {
        if (!keySet.contains(columnName)) {
            return columnName;
        }

        return createAlias(columnName + "_1", keySet);
    }

}
