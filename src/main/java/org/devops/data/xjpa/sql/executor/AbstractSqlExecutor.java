package org.devops.data.xjpa.sql.executor;

import org.devops.data.xjpa.constant.XjpaConstant;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.sql.executor.session.ExecuteSession;
import org.devops.data.xjpa.sql.logger.SqlLogger;
import org.devops.data.xjpa.table.EntityTableField;
import org.devops.data.xjpa.util.PreparedStatementUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 默认执行
 */
public abstract class AbstractSqlExecutor<K, V> implements ISqlExecutor<K, V> {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractSqlExecutor.class);

    /**
     *
     */
    protected final ExecuteSession executeSession;

    protected final SqlLogger sqlLogger;

    public AbstractSqlExecutor(ExecuteSession executeSession, SqlLogger sqlLogger) {
        this.executeSession = executeSession;
        this.sqlLogger = sqlLogger;
    }


    /**
     * @param sortHandler
     * @return
     */
    protected String concatSortString(SortHandler sortHandler) {
        if (sortHandler == null || !sortHandler.requiredSort()) {
            return "";
        }
        return " order by " + (sortHandler.sortRequestList().stream()
                .map(sortSet -> {
                    if (SortType.plain == sortSet.getSortType()) {
                        return sortSet.getColumn();
                    }
                    return "`" + sortSet.getColumn() + "` " + sortSet.getSortType().getOperator();
                })
                .collect(Collectors.joining(XjpaConstant.COMMA_MARK)));
    }

    /**
     * @param limitHandler
     * @return
     */
    protected String concatLimitString(LimitHandler limitHandler) {
        if (limitHandler == null || limitHandler.getStart() < 0) {
            return "";
        }
        return " limit " + limitHandler.getStart() +
                (limitHandler.getLimit() > 0 ? "," + limitHandler.getLimit() : "");
    }

    /**
     * 确保参数长度一致
     *
     * @param processSqlList
     */
    protected void ensureSqlParameterSameLength(List<? extends ProcessSql> processSqlList) {
        int size = processSqlList.get(0).getStatementParameters().size();
        if (processSqlList.stream()
                .anyMatch(processSql -> processSql.getStatementParameters().size() != size)) {
            throw new XjpaExecuteException("sql parameter length error");
        }
    }


    /**
     * @param preparedSql
     * @param processSqlList
     * @return
     * @throws SQLException
     */
    protected int executeBatchUpdate(String preparedSql, List<? extends ProcessSql> processSqlList) throws SQLException {
        List<Map<Integer, Object>> parameters = processSqlList.stream()
                .map(ProcessSql::getStatementParameters)
                .collect(Collectors.toList());

        return doExecuteBatchUpdate(preparedSql, parameters, null);
    }

    /**
     * @param preparedSql
     * @param processSqlList
     * @param generatedKeysConsumer
     * @return
     * @throws SQLException
     */
    protected int executeBatchUpdate(String preparedSql, List<? extends ProcessSql> processSqlList,
                                     Consumer<ResultSet> generatedKeysConsumer) throws SQLException {
        List<Map<Integer, Object>> parameters = processSqlList.stream()
                .map(ProcessSql::getStatementParameters)
                .collect(Collectors.toList());

        return doExecuteBatchUpdate(preparedSql, parameters, generatedKeysConsumer);
    }



    /**
     * @param preparedSql
     * @param valuesList
     * @param generatedKeysConsumer
     * @return
     * @throws SQLException
     */
    protected int doExecuteBatchUpdate(String preparedSql, List<Map<Integer, Object>> valuesList, Consumer<ResultSet> generatedKeysConsumer) throws SQLException {
        PreparedStatement preparedStatement = executeSession.updateStatement(preparedSql);
        for (Map<Integer, Object> values : valuesList) {
            PreparedStatementUtil.setParameters(preparedStatement, values);
            preparedStatement.addBatch();
        }
        sqlLogger.logSql(preparedSql, valuesList);
        long start = System.currentTimeMillis();
        int[] executeBatchResult = preparedStatement.executeBatch();
        long end = System.currentTimeMillis();
        int batchUpdatedRow = Arrays.stream(executeBatchResult).reduce(0, Integer::sum);
        sqlLogger.logAffect(batchUpdatedRow, start, end);
        if (generatedKeysConsumer != null) {
            generatedKeysConsumer.accept(preparedStatement.getGeneratedKeys());
        }
        return batchUpdatedRow;
    }


    /**
     * @param preparedSql
     * @param values
     * @return
     * @throws SQLException
     */
    protected int doExecuteUpdate(String preparedSql, Map<Integer, Object> values) throws SQLException {
        PreparedStatement preparedStatement = executeSession.updateStatement(preparedSql);
        PreparedStatementUtil.setParameters(preparedStatement, values);
        sqlLogger.logSql(preparedSql, values);
        long start = System.currentTimeMillis();
        int updatedRow = preparedStatement.executeUpdate();
        long end = System.currentTimeMillis();
        sqlLogger.logAffect(updatedRow, start, end);
        return updatedRow;
    }

    /**
     * @param preparedSql
     * @param values
     * @param generatedKeysConsumer
     * @return
     * @throws SQLException
     */
    protected int doExecuteUpdate(String preparedSql, Map<Integer, Object> values, Consumer<ResultSet> generatedKeysConsumer) throws SQLException {
        PreparedStatement preparedStatement = executeSession.updateStatement(preparedSql);
        PreparedStatementUtil.setParameters(preparedStatement, values);
        sqlLogger.logSql(preparedSql, values);
        long start = System.currentTimeMillis();
        int updatedRow = preparedStatement.executeUpdate();
        long end = System.currentTimeMillis();
        sqlLogger.logAffect(updatedRow, start, end);
        if (generatedKeysConsumer != null) {
            generatedKeysConsumer.accept(preparedStatement.getGeneratedKeys());
        }
        return updatedRow;
    }


    /**
     * 为空的主键跳过
     * @param entity
     * @param entityTableField
     * @return 是否为主键并且为空值
     */
    boolean isNullPrimaryKey(V entity, EntityTableField entityTableField) {
        if (!entityTableField.isPriKey()) {
            return false;
        }

        PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(entity.getClass(), entityTableField.getJavaField().getName());
        if (propertyDescriptor == null) {
            return false;
        }
        try {
            Object val = propertyDescriptor.getReadMethod().invoke(entity);
            if (val == null) {
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException ignored) {}
        return false;
    }
}
