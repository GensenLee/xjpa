package org.devops.data.xjpa.sql.executor;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 删除sql
 */
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
@Getter
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
}
