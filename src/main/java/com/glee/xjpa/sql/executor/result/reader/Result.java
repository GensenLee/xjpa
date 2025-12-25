package com.glee.xjpa.sql.executor.result.reader;

import com.glee.xjpa.repository.impl.RepositoryContext;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/7
 * @description query结果
 */
@SuppressWarnings({"rawtypes"})
public interface Result {

    int getAffectRow();

    List<Map<String, Object>> getRawResults();

    /**
     * @author GENSEN
     * @date 2022/11/7
     * @description ModelResultSet构造
     */
    class Builder {

        private final RepositoryContext context;

        private Builder(RepositoryContext context) {
            this.context = context;
        }

        /**
         * @param resultSet
         * @return
         */
        public static Result build(ResultSet resultSet) {
            return ResultSetResult.read(resultSet);
        }

        /**
         * @param affectRow
         * @return
         */
        public static Result build(int affectRow) {
            return new AffectRowResult(affectRow);
        }

    }
}
