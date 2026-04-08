package com.glee.xjpa.io.update;

import cn.hutool.core.collection.CollectionUtil;
import com.glee.xjpa.constant.XJpaConstant;
import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.usermodel.QueryWhere;
import com.glee.xjpa.table.EntityTableField;
import com.glee.xjpa.table.TableProperties;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2026/4/1
 * @description
 */
public class UpdateSqlTemplate {


    private final TableProperties<? ,?> tableProperties;

    public UpdateSqlTemplate(TableProperties<?, ?> tableProperties) {
        this.tableProperties = tableProperties;
    }

    public String toUpdateTemplate(UpdateSets updateSets, QueryWhere where) {
        List<UpdateSetClause> updateFieldList = updateSets.getUpdateFields();
        if (CollectionUtil.isEmpty(updateFieldList)) {
            throw new XJpaException("update set can not be empty");
        }



        return "update " +
                tableProperties.getMetadata().getTableName() +
                " set " +
                updateFieldList.stream()
                        .map(UpdateSetClause::toSqlClause)
                        .collect(Collectors.joining(XJpaConstant.COMMA_MARK)) +
                " where " +
                QueryWhereUtil.toWhereString(where);
    }

    public String toUpdateTemplate(List<EntityTableField> updateFieldList) {
        EntityTableField primaryKeyField = tableProperties.getMetadata().getPrimaryKeyField();

        StringBuilder sqlBuilder = new StringBuilder("update ");
        sqlBuilder.append(tableProperties.getMetadata().getTableName())
                .append(" set ");

        String joiningString = updateFieldList.stream()
                .map(pkf -> pkf.column().name() + " = ?")
                .collect(Collectors.joining(XJpaConstant.COMMA_MARK));
        sqlBuilder.append(joiningString)
                .append(" where ")
                .append(primaryKeyField.column().name())
                .append(" = ?");

        return sqlBuilder.toString();
    }

}
