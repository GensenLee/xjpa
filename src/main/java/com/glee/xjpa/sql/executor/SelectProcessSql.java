package com.glee.xjpa.sql.executor;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 查询 sql
 */
public class SelectProcessSql implements ProcessSql {

    /**
     * 最终使用的sql
     */
    private String finalSql;

    /**
     * 下标      值
     * index -> param value
     */
    private final Map<Integer, Object> finalSqlParameters = new HashMap<>();

    /**
     * where 条件
     */
    private String whereString;

    /**
     * where 条件参数
     * index -> param value
     */
    private Map<Integer, Object> whereParameters;

    public static ProcessSqlBuilder builder() {
        return ProcessSqlBuilder.aProcessSql();
    }


    /**
     * @param values
     */
    public void appendFinalSqlParameters(Map<Integer, Object> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        int startIndex = finalSqlParameters.size();
        for (Map.Entry<Integer, Object> entry : values.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Collection) {
                for (Object v : ((Collection<?>) value)) {
                    finalSqlParameters.put(++startIndex, v);
                }
            }else {
                finalSqlParameters.put(++startIndex, value);
            }
        }
    }

    @Override
    public String getStatementSql() {
        return finalSql;
    }

    @Override
    public Map<Integer, Object> getStatementParameters() {
        return finalSqlParameters;
    }

    void setFinalSql(String finalSql) {
        this.finalSql = finalSql;
    }

    void setWhereString(String whereString) {
        this.whereString = whereString;
    }

    void setWhereParameters(Map<Integer, Object> whereParameters) {
        this.whereParameters = whereParameters;
    }

    public String getFinalSql() {
        return finalSql;
    }

    public Map<Integer, Object> getFinalSqlParameters() {
        return finalSqlParameters;
    }

    public String getWhereString() {
        return whereString;
    }

    public Map<Integer, Object> getWhereParameters() {
        return whereParameters;
    }

    public static final class ProcessSqlBuilder {
        private String finalSql;
        private String whereString;
        private Map<Integer, Object> whereParameters;

        private final List<Map<Integer, Object>> parameterList = new ArrayList<>();


        private ProcessSqlBuilder() {
        }

        public static ProcessSqlBuilder aProcessSql() {
            return new ProcessSqlBuilder();
        }

        public ProcessSqlBuilder withFinalSql(String finalSql) {
            this.finalSql = finalSql;
            return this;
        }

        public ProcessSqlBuilder withWhereString(String whereString) {
            this.whereString = whereString;
            return this;
        }

        public ProcessSqlBuilder withWhereParameters(Map<Integer, Object> whereParameters) {
            this.whereParameters = whereParameters;
            this.parameterList.add(whereParameters);
            return this;
        }

        public SelectProcessSql build() {
            SelectProcessSql processSql = new SelectProcessSql();
            processSql.setFinalSql(finalSql);
            processSql.setWhereString(whereString);
            processSql.setWhereParameters(whereParameters);
            for (Map<Integer, Object> params : parameterList) {
                processSql.appendFinalSqlParameters(params);
            }
            return processSql;
        }
    }
}
