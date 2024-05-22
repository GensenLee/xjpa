package org.devops.data.xjpa.util;

import com.mysql.cj.MysqlType;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.springframework.util.CollectionUtils;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description PreparedStatement工具
 */
public class PreparedStatementUtil {

    /**
     * @param preparedStatement
     * @param value
     * @param index
     */
    public static void setParameter(PreparedStatement preparedStatement, Object value, int index) {

        try {
            Object setVal = value;
            int jdbcType = JDBCType.VARCHAR.getVendorTypeNumber();
            if (value instanceof PstParameter) {
                // 2022-12-12 借助 PstParameter 接收空值，因为设置空值需要提供jdbcType参数
                setVal = ((PstParameter) value).getValue();
                String fullMysqlTypeName = ((PstParameter) value).getEntityTableField().getTableFieldMetadata().getType();
                jdbcType = MysqlType.getByName(fullMysqlTypeName).getJdbcType();
            }

            if (setVal == null) {
                preparedStatement.setNull(index, jdbcType);
            }else {
                preparedStatement.setObject(index, setVal);
            }

        } catch (SQLException e) {
            throw new XjpaExecuteException(e);
        }

    }

    /**
     * @param preparedStatement
     * @param valueList
     */
    public static void setParameters(PreparedStatement preparedStatement, List<Object> valueList) {
        if (CollectionUtils.isEmpty(valueList)) {
            return;
        }
        try {
            preparedStatement.clearParameters();
        } catch (SQLException ignored) {}
        int index = 1;
        for (Object val : valueList) {
            setParameter(preparedStatement, val, index++);
        }
    }

    public static void setParameters(PreparedStatement preparedStatement, Map<Integer, Object> valueList) {
        if (CollectionUtils.isEmpty(valueList)) {
            return;
        }
        try {
            preparedStatement.clearParameters();
        } catch (SQLException ignored) {}
        for (Map.Entry<Integer, Object> entry : valueList.entrySet()) {
            setParameter(preparedStatement, entry.getValue(), entry.getKey());
        }
    }

}
