package org.devops.data.xjpa.sql.executor;

import java.sql.ResultSet;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/11/9
 * @description 执行数据
 */
public interface ProcessSql {

    String getStatementSql();

    Map<Integer, Object> getStatementParameters();

}
