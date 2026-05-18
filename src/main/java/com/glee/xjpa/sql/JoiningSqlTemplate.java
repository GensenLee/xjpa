package com.glee.xjpa.sql;

import com.glee.xjpa.io.JoiningGroupByQuery;
import com.glee.xjpa.io.JoiningQuery;
import com.glee.xjpa.io.QueryPage;
import com.glee.xjpa.io.groupby.JoiningGroupBy;
import com.glee.xjpa.io.having.JoiningGroupByHave;
import com.glee.xjpa.io.include.JoiningIncludeBy;
import com.glee.xjpa.io.join.AbstractJoinOn;
import com.glee.xjpa.io.join.JoinPoint;
import com.glee.xjpa.io.join.XJpaJoiningRepository;
import com.glee.xjpa.io.orderby.JoiningOrderBy;
import com.glee.xjpa.sql.where.QueryWhereUtil;
import com.glee.xjpa.sql.where.objects.IQueryWhereObject;
import jakarta.persistence.criteria.JoinType;

import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2026/5/18
 * @description 连表查询 SQL 模板
 */
public class JoiningSqlTemplate {

    private final StringBuilder sqlBuilder;

    public JoiningSqlTemplate(String prefix) {
        this.sqlBuilder = new StringBuilder(prefix);
    }

    public void setDistinct(boolean distinct) {
        if (distinct) {
            sqlBuilder.append(" distinct");
        }
    }

    public void setIncludeBy(JoiningIncludeBy includeBy) {
        if (includeBy == null) {
            sqlBuilder.append(" *");
        } else {
            sqlBuilder.append(" ").append(includeBy.toSqlClause());
        }
    }

    public void setFromClause(String fromClause) {
        sqlBuilder.append(" from ").append(fromClause);
    }

    public void setJoinClause(String joinClause) {
        sqlBuilder.append(joinClause);
    }

    public void setWhere(IQueryWhereObject where) {
        if (where == null || where.isEmpty()) {
            return;
        }
        sqlBuilder.append(" where ").append(QueryWhereUtil.toWhereString(where));
    }

    public void setGroupBy(JoiningGroupBy groupBy) {
        if (groupBy == null) {
            return;
        }
        sqlBuilder.append(" group by ").append(groupBy.toSqlClause());
    }

    public void setHaving(JoiningGroupByHave groupByHaving) {
        if (groupByHaving == null) {
            return;
        }
        sqlBuilder.append(" having ").append(groupByHaving.toSqlClause());
    }

    public void setOrderBy(JoiningOrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        sqlBuilder.append(" order by ").append(orderBy.toSqlClause());
    }

    public void setPage(QueryPage page) {
        if (page != null) {
            sqlBuilder.append(" limit ").append(page.start()).append(",").append(page.size());
        }
    }

    public String getSqlString() {
        return sqlBuilder.toString();
    }

    /**
     * 构建连表查询 SQL
     */
    public static String buildJoinSql(JoiningQuery query, String fromClause, String joinClause) {
        JoiningSqlTemplate template = new JoiningSqlTemplate("select");
        template.setDistinct(query.isDistinct());
        template.setIncludeBy(query.getIncluding());
        template.setFromClause(fromClause);
        template.setJoinClause(joinClause);
        template.setWhere(query.getWhere());
        template.setOrderBy(query.getOrderBy());
        template.setPage(query.getQueryPage());
        return template.getSqlString();
    }

    /**
     * 构建连表分组查询 SQL
     */
    public static String buildJoinSql(JoiningGroupByQuery query, String fromClause, String joinClause) {
        JoiningSqlTemplate template = new JoiningSqlTemplate("select");
        template.setDistinct(query.isDistinct());
        template.setIncludeBy(query.getIncluding());
        template.setFromClause(fromClause);
        template.setJoinClause(joinClause);
        template.setWhere(query.getWhere());
        template.setGroupBy(query.getJoiningGroupBy());
        template.setHaving(query.getGroupByHave());
        template.setOrderBy(query.getOrderBy());
        template.setPage(query.getQueryPage());
        return template.getSqlString();
    }

    /**
     * 构建连表查询 Count SQL
     */
    public static String buildCountSql(JoiningQuery query, String fromClause, String joinClause) {
        return buildCountSql(query, fromClause, joinClause, null);
    }

    /**
     * 构建连表查询 Count SQL
     */
    public static String buildCountSql(JoiningQuery query, String fromClause, String joinClause, String countColumn) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        if (countColumn != null) {
            sql.append("count(").append(countColumn).append(")");
        } else {
            sql.append("count(*)");
        }
        sql.append(" from ").append(fromClause);
        sql.append(joinClause);

        if (query.getWhere() != null && !query.getWhere().isEmpty()) {
            sql.append(" where ").append(QueryWhereUtil.toWhereString(query.getWhere()));
        }

        return sql.toString();
    }

    /**
     * 构建连表分组查询 Count SQL
     */
    public static String buildCountSql(JoiningGroupByQuery query, String fromClause, String joinClause) {
        return buildCountSql(query, fromClause, joinClause, null);
    }

    /**
     * 构建连表分组查询 Count SQL
     */
    public static String buildCountSql(JoiningGroupByQuery query, String fromClause, String joinClause, String countColumn) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        if (countColumn != null) {
            sql.append("count(").append(countColumn).append(")");
        } else {
            sql.append("count(*)");
        }
        sql.append(" from ").append(fromClause);
        sql.append(joinClause);

        if (query.getWhere() != null && !query.getWhere().isEmpty()) {
            sql.append(" where ").append(QueryWhereUtil.toWhereString(query.getWhere()));
        }

        if (query.getJoiningGroupBy() != null) {
            sql.append(" group by ").append(query.getJoiningGroupBy().toSqlClause());
        }

        if (query.getGroupByHave() != null) {
            sql.append(" having ").append(query.getGroupByHave().toSqlClause());
        }

        return sql.toString();
    }

    /**
     * 从 Repository 获取生成的 SQL
     */
    public static String getGeneratedSql(XJpaJoiningRepository repository, JoiningQuery query) {
        String fromClause = buildFromClause(repository);
        String joinClause = buildJoinClause(repository);
        return buildJoinSql(query, fromClause, joinClause);
    }

    /**
     * 从 Repository 获取生成的 SQL（支持 Group By）
     */
    public static String getGeneratedSql(XJpaJoiningRepository repository, JoiningGroupByQuery query) {
        String fromClause = buildFromClause(repository);
        String joinClause = buildJoinClause(repository);
        return buildJoinSql(query, fromClause, joinClause);
    }

    /**
     * 构建 FROM 子句
     */
    public static String buildFromClause(XJpaJoiningRepository repository) {
        List<JoinPoint> joinPointOrder = repository.getJoinPointOrder();
        if (joinPointOrder.isEmpty()) {
            throw new IllegalArgumentException("No join points defined");
        }
        JoinPoint drivingTable = joinPointOrder.get(0);
        return drivingTable.getTableName() + " as " + drivingTable.getTableAlias();
    }

    /**
     * 构建 JOIN 子句
     */
    public static String buildJoinClause(XJpaJoiningRepository repository) {
        StringBuilder sql = new StringBuilder();
        List<JoinPoint> joinPointOrder = repository.getJoinPointOrder();
        Map<JoinPoint, AbstractJoinOn> joinOnMap = repository.getJoinOnMap();

        for (int i = 1; i < joinPointOrder.size(); i++) {
            JoinPoint joinPoint = joinPointOrder.get(i);
            AbstractJoinOn joinOn = joinOnMap.get(joinPoint);

            if (joinOn == null) {
                continue;
            }

            JoinType joinType = joinOn.getJoinType();
            String joinTypeStr;
            if (joinType == JoinType.LEFT) {
                joinTypeStr = "left join";
            } else if (joinType == JoinType.RIGHT) {
                joinTypeStr = "right join";
            } else if (joinType == JoinType.INNER) {
                joinTypeStr = "inner join";
            } else {
                joinTypeStr = "left join";
            }

            sql.append(" ").append(joinTypeStr).append(" ");
            sql.append(joinPoint.getTableName()).append(" as ").append(joinPoint.getTableAlias());

            sql.append(" on (");
            var joiningOnColumns = joinOn.getJoiningOnColumns();
            if (joiningOnColumns != null && !joiningOnColumns.isEmpty()) {
                boolean first = true;
                for (var pair : joiningOnColumns) {
                    if (!first) {
                        sql.append(" and ");
                    }
                    String leftColumn = pair.getKey();
                    String rightColumn = pair.getValue();
                    sql.append(joinPointOrder.get(0).getTableAlias()).append(".`").append(rightColumn).append("`");
                    sql.append(" = ");
                    sql.append(joinPoint.getTableAlias()).append(".`").append(leftColumn).append("`");
                    first = false;
                }
            }
            sql.append(")");
        }

        return sql.toString();
    }

}