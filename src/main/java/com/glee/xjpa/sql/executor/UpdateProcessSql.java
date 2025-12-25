package com.glee.xjpa.sql.executor;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 更新sql
 */
public class UpdateProcessSql implements ProcessSql{

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

    /**
     * set 值设置
     */
    private String setValueString;

    /**
     * set 值参数
     * index -> param value
     */
    private Map<Integer, Object> setValueParameters;

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
        private String setValueString;
        private Map<Integer, Object> setValueParameters;

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

        public ProcessSqlBuilder withSetValueString(String setValueString) {
            this.setValueString = setValueString;
            return this;
        }

        public ProcessSqlBuilder withSetValueParameters(Map<Integer, Object> setValueParameters) {
            this.setValueParameters = setValueParameters;
            this.parameterList.add(setValueParameters);
            return this;
        }

        public UpdateProcessSql build() {
            UpdateProcessSql processSql = new UpdateProcessSql();
            processSql.setFinalSql(finalSql);
            processSql.setWhereString(whereString);
            processSql.setWhereParameters(whereParameters);
            processSql.setSetValueString(setValueString);
            processSql.setSetValueParameters(setValueParameters);
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

    void setSetValueString(String setValueString) {
        this.setValueString = setValueString;
    }

    void setSetValueParameters(Map<Integer, Object> setValueParameters) {
        this.setValueParameters = setValueParameters;
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

    public String getSetValueString() {
        return setValueString;
    }

    public Map<Integer, Object> getSetValueParameters() {
        return setValueParameters;
    }
}
