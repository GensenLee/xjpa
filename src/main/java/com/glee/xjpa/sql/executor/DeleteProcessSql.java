package com.glee.xjpa.sql.executor;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 删除sql
 */
public class DeleteProcessSql implements ProcessSql {

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
            finalSqlParameters.put(++startIndex, entry.getValue());
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


        public DeleteProcessSql build() {
            DeleteProcessSql processSql = new DeleteProcessSql();
            processSql.setFinalSql(finalSql);
            processSql.setWhereString(whereString);
            processSql.setWhereParameters(whereParameters);
            for (Map<Integer, Object> params : parameterList) {
                processSql.appendFinalSqlParameters(params);
            }
            return processSql;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteProcessSql that = (DeleteProcessSql) o;
        return Objects.equals(finalSql, that.finalSql) &&
                Objects.equals(finalSqlParameters, that.finalSqlParameters) &&
                Objects.equals(whereString, that.whereString) &&
                Objects.equals(whereParameters, that.whereParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finalSql, finalSqlParameters, whereString, whereParameters);
    }
}
