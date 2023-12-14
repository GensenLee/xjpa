package org.devops.data.xjpa.join;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.constant.CommonConstant;
import org.devops.core.utils.spring.SpringContextUtil;
import org.devops.core.utils.util.ListUtil;
import org.devops.core.utils.util.LongUtil;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.lifecycle.XjpaRepositoryRegister;
import org.devops.data.xjpa.repository.StandardJpaRepository;
import org.devops.data.xjpa.repository.impl.DefaultRepositoryController;
import org.devops.data.xjpa.repository.impl.RepositoryController;
import org.devops.data.xjpa.repository.impl.enhance.DisposableEnhanceCurdBound;
import org.devops.data.xjpa.sql.executor.LimitHandler;
import org.devops.data.xjpa.sql.executor.SortHandler;
import org.devops.data.xjpa.sql.executor.SortType;
import org.devops.data.xjpa.sql.result.parser.ResultParser;
import org.devops.data.xjpa.sql.where.XQueryWhereExplorer;
import org.devops.data.xjpa.sql.where.handler.SoftDeleteConfig;
import org.devops.data.xjpa.table.TableFieldMetadata;
import org.devops.data.xjpa.table.TableMetadata;
import org.devops.data.xjpa.util.SqlExecutorUtil;
import org.devops.data.xjpa.util.TableUtil;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2023/6/21
 * @description 组合连接
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class MultipleJoinModel extends AbstractJoinModel {

    private final static String LEFT = "lt";
    private final static String RIGHT = "rt";

    protected final JoinType joinType;


    /**
     * 右表
     */
    protected final Class<?> rightEntityType;

    protected final StandardJpaRepository leftRepository;

    protected final XjpaRepositoryRegister repositoryRegister;


    protected MultipleJoinModel(StandardJpaRepository repository, JoinType joinType, Class<?> rightEntityType) {
        super(repository, new JoinQueryWhere(),
                new DisposableEnhanceCurdBound(new DefaultRepositoryController(repository).getContext()));
        this.leftRepository = repository;
        this.joinType = joinType;
        this.rightEntityType = rightEntityType;
        repositoryRegister = SpringContextUtil.getBean(XjpaRepositoryRegister.class);
    }

    @Override
    String getTableAlisa(JoinTable joinTable) {
        return JoinTable.LEFT == joinTable ? LEFT : RIGHT;
    }

    @Override
    String getJoinLabel() {
        return joinType.grammar;
    }


    @Override
    public long count() {
        String includedString = formatIncludedString();
        String whereString = formatWhereString();
        String groupString = formatGroupString();
        String havingString = formatHavingString();
        String orderString = formatOrderString();
        String limitString = "1";

        String sql = formatSql(String.format("count(%s) as __count__", StringUtil.getDefault(includedString,"*")),
                whereString, groupString, havingString, orderString, limitString);

        log.info("count sql: {}", sql);

        List<Map> result = queryForType(sql, Map.class);
        Map countResult = CollectionUtils.firstElement(result);
        long count = Optional.ofNullable(countResult)
                .map(ct -> ct.getOrDefault("__count__", 0L))
                .map(LongUtil::toLong)
                .orElse(0L);

        log.info("count result: {}", count);

        return count;
    }

    @Override
    public boolean isExist() {
        String includedString = formatIncludedString();
        String whereString = formatWhereString();
        String groupString = formatGroupString();
        String havingString = formatHavingString();
        String orderString = formatOrderString();
        String limitString = "1";

        String sql = formatSql(StringUtil.getDefault(includedString, "*"),
                whereString, groupString, havingString, orderString, limitString);

        log.info("isExist sql: {}", sql);

        List<Map> result = queryForType(sql, Map.class);

        boolean isExist = !CollectionUtils.isEmpty(result);

        log.info("isExist result: {}", isExist);

        return isExist;
    }

    @Override
    public <T> List<T> listSingleColumn(Class<T> type) {
        String includedString = formatIncludedString();
        String whereString = formatWhereString();
        String groupString = formatGroupString();
        String havingString = formatHavingString();
        String orderString = formatOrderString();
        String limitString = formatLimitString();

        String sql = formatSql(includedString, whereString, groupString, havingString, orderString, limitString);

        log.info("list sql: {}", sql);

        List<T> result = querySingleForType(sql, type);

        log.info("total row: {}", result.size());

        return result;
    }

    @Override
    public <T> T getSingleColumn(Class<T> type) {
        String includedString = formatIncludedString();
        String whereString = formatWhereString();
        String groupString = formatGroupString();
        String havingString = formatHavingString();
        String orderString = formatOrderString();

        String sql = formatSql(includedString, whereString, groupString, havingString, orderString, "1");

        log.info("list sql: {}", sql);

        List<T> result = querySingleForType(sql, type);

        log.info("total row: {}", result.size());

        return CollectionUtils.firstElement(result);
    }


    @Override
    public List<Map<String, Object>> list() {
        List<Map> rawResult = select(Map.class, formatIncludedString());

        return rawResult.stream()
                .map(map -> (Map<String, Object>) map)
                .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> list(Class<T> type) {
        return select(type, formatIncludedString());
    }

    <T> List<T> select(Class<T> resultType, String includedString) {

        String whereString = formatWhereString();
        String groupString = formatGroupString();
        String havingString = formatHavingString();
        String orderString = formatOrderString();
        String limitString = formatLimitString();

        String sql = formatSql(includedString, whereString, groupString, havingString, orderString, limitString);

        log.info("list sql: {}", sql);

        List<T> result = queryForType(sql, resultType);

        log.info("total row: {}", result.size());

        return result;
    }

    protected <T> List<T> queryForType(String sql, Class<T> resultType) {
        List<Map<String, Object>> mapList = SqlExecutorUtil.query(sql, repository);

        if (ListUtil.isNull(mapList)) {
            return Collections.emptyList();
        }

        if (Map.class == resultType) {
            return (List<T>) mapList;
        }

        RepositoryController controller = new DefaultRepositoryController(repository);
        ResultParser resultParser = controller.getContext().getResultParser();
        return resultParser.parse(mapList, resultType);
    }

    protected <T> List<T> querySingleForType(String sql, Class<T> resultType) {
        List<Map<String, Object>> mapList = SqlExecutorUtil.query(sql, repository);

        if (ListUtil.isNull(mapList)) {
            return Collections.emptyList();
        }

        Map<String, Object> objectMap = mapList.get(0);

        String singleReadColumn = getSingleReadColumn(objectMap.keySet());

        RepositoryController controller = new DefaultRepositoryController(repository);
        ResultParser resultParser = controller.getContext().getResultParser();
        return resultParser.parseSingleColumn(mapList, singleReadColumn, resultType);
    }

    String formatSql(String includeString, String whereString, String groupString,
                     String havingString, String orderString, String limitString) {

        StringBuilder stringBuilder = new StringBuilder("select ")
                .append(includeString)
                .append(" from ")
                .append(getLeftTableName()).append(" as ").append(LEFT).append(" ")
                .append(getJoinLabel()).append(" ")
                .append(getRightTableName()).append(" as ").append(RIGHT)
                .append(" on ").append(formatOnString());

        if (StringUtil.isNotEmpty(whereString)) {
            stringBuilder.append(" where ").append(whereString);
        }

        if (StringUtil.isNotEmpty(groupString)) {
            stringBuilder.append(" group by ").append(groupString);
        }

        if (StringUtil.isNotEmpty(havingString)) {
            stringBuilder.append(" having ").append(havingString);
        }

        if (StringUtil.isNotEmpty(orderString)) {
            stringBuilder.append(" order by ").append(orderString);
        }

        if (StringUtil.isNotEmpty(limitString)) {
            stringBuilder.append(" limit ").append(limitString);
        }

        return stringBuilder.toString();
    }


    /**
     * @param resultKeySet 查询结果的列
     * @return
     */
    private String getSingleReadColumn(Set<String> resultKeySet) {
        String includedColumn = null;
        TableColumn tableColumn = Optional.ofNullable(CollectionUtils.firstElement(distinctTableColumns))
                .orElse(CollectionUtils.firstElement(includedTableColumns));
        if (tableColumn instanceof AliasTableColumn) {
            includedColumn = ((AliasTableColumn) tableColumn).getColumnAlias();
        }else if (tableColumn instanceof JoinTableColumn) {
            includedColumn = ((JoinTableColumn) tableColumn).getColumn();
        }else if (!CollectionUtils.isEmpty(enhanceCurdBound.getIncludeColumns())) {
            includedColumn = String.valueOf(enhanceCurdBound.getIncludeColumns().iterator().next());
        }
        if (resultKeySet.contains(includedColumn)) {
            return includedColumn;
        }

        return CollectionUtils.firstElement(resultKeySet);
    }


    private String formatIncludedString() {

        // distinct 方式
        String result = visitAndJoiningColumns(distinctTableColumns, MultipleJoinModel.this);
        Collection<String> distinctColumns = enhanceCurdBound.getDistinctColumns();
        if (!CollectionUtils.isEmpty(distinctColumns)) {
            if (StringUtil.isNotEmpty(result)) {
                result = result + CommonConstant.COMMA_MARK + String.join(CommonConstant.COMMA_MARK, distinctColumns);
            }else {
                result = String.join(CommonConstant.COMMA_MARK, distinctColumns);
            }
        }
        if (StringUtil.isNotEmpty(result)) {
            return "distinct " + result;
        }

        // 非 distinct 方式

        result = visitAndJoiningColumns(includedTableColumns, MultipleJoinModel.this);

        // 自定义字段
        Collection includeColumns = enhanceCurdBound.getIncludeColumns();
        if (!CollectionUtils.isEmpty(includeColumns)) {
            if (StringUtil.isNotEmpty(result)) {
                result = result + CommonConstant.COMMA_MARK + String.join(CommonConstant.COMMA_MARK, includeColumns);
            }else {
                result = String.join(CommonConstant.COMMA_MARK, includeColumns);
            }
        }


        if (StringUtil.isEmpty(result)) {
            return CommonConstant.ASTERISK_MARK;
        }
        return result;
    }



    private String visitAndJoiningColumns(List<TableColumn> includedTableColumns, MultipleJoinModel joinModel) {
        return includedTableColumns.stream()
                .map(c -> {
                    if (c instanceof JoinTableVisitor) {
                        ((JoinTableVisitor) c).visit(joinModel);
                    }
                    return c.getColumnLabel();
                })
                .collect(Collectors.joining(CommonConstant.COMMA_MARK));
    }

    private String getLeftTableName() {
        RepositoryController controller = new DefaultRepositoryController(repository);
        return TableUtil.getTableNameByEntityType(controller.getContext().getEntityTable().entityType());
    }

    private String getRightTableName() {
        return TableUtil.getTableNameByEntityType(rightEntityType);
    }

    private String formatOnString() {
        if (onFieldMapping.isEmpty()) {
            throw new XjpaExecuteException(" join table 'on (lt.a=rt.b)' much set");
        }

        // 右表逻辑删除
        Optional<SoftDeleteConfig> rightTableSoftDeleteConfigOptional =
                Optional.ofNullable(repositoryRegister.findRegisteredRepositoryOfEntity(rightEntityType))
                        .map(r -> new DefaultRepositoryController(r).getContext().getSingleton(SoftDeleteConfig.class));

        rightTableSoftDeleteConfigOptional.ifPresent(softDeleteConfig -> {
            if (softDeleteConfig.isEnabled()) {
                onFieldMapping.put(ColumnDef.ofRight(softDeleteConfig.getColumn()), new WhereValue(softDeleteConfig.getNotDeleteValue()));
            }
        });

        for (Map.Entry<TableColumn, TableColumn> entry : onFieldMapping.entrySet()) {
            TableColumn key = entry.getKey();

            if (key instanceof JoinTableVisitor) {
                ((JoinTableVisitor) key).visit(this);
            }

            TableColumn value = entry.getValue();

            if (value instanceof JoinTableVisitor) {
                ((JoinTableVisitor) value).visit(this);
            }
        }
        return "(" + onFieldMapping.entrySet().stream()
                .map(e -> e.getKey().getColumnLabel() + " = " +  e.getValue().getColumnLabel())
                .collect(Collectors.joining(" and ")) + ")";
    }

    private String formatColumn(String tableAlise, String column) {
        return tableAlise + CommonConstant.POINT_MARK + "`" + column + "`";
    }

    private String formatWhereString() {
        if (rootWhere.isEmpty()) {
            return null;
        }

        // 设置逻辑删除

        // 左表
        RepositoryController controller = new DefaultRepositoryController(leftRepository);
        Optional<SoftDeleteConfig> leftTableSoftDeleteConfigOptional =
                Optional.ofNullable(controller.getContext().getSingleton(SoftDeleteConfig.class));
        leftTableSoftDeleteConfigOptional.ifPresent(softDeleteConfig -> {
            if (softDeleteConfig.isEnabled()) {
                rootWhere.andEqual(ColumnDef.ofLeft(softDeleteConfig.getColumn()), softDeleteConfig.getNotDeleteValue());
            }
        });

        // 2023-07-05 where后加逻辑删除条件会导致两表链接后再一并过滤，右表无数据时导致不返回左表数据
//        // 右表
//        Optional<SoftDeleteConfig> rightTableSoftDeleteConfigOptional =
//                Optional.ofNullable(repositoryRegister.findRegisteredRepositoryOfEntity(rightEntityType))
//                .map(r -> new DefaultRepositoryController(r).getContext().getSingleton(SoftDeleteConfig.class));
//
//        rightTableSoftDeleteConfigOptional.ifPresent(softDeleteConfig -> {
//            rootWhere.andEqual(ColumnDef.ofRight(softDeleteConfig.getColumn()), softDeleteConfig.getNotDeleteValue());
//        });

        XQueryWhereExplorer explorer = new JoinTableQueryWhereExplorer(this);

        rootWhere.accept(explorer);

        return explorer.getWhereString();
    }


    private String formatGroupString() {
        Collection<String> groupingColumns = enhanceCurdBound.getGroupingColumns();
        String result = String.join(CommonConstant.COMMA_MARK, groupingColumns);

        if (groupingTableColumns.isEmpty()) {
            return result;
        }

        result += visitAndJoiningColumns(groupingTableColumns, MultipleJoinModel.this);

        return result;
    }

    private String formatHavingString() {
        return enhanceCurdBound.getHavingString();
    }

    private String formatOrderString() {

        SortHandler sortHandler = enhanceCurdBound.getSortHandler();
        if (!sortHandler.requiredSort()) {
            return null;
        }

        return sortHandler.sortRequestList().stream()
                .map(sortSet -> {
                    if (SortType.plain == sortSet.getSortType()) {
                        return sortSet.getColumn();
                    }
                    return concatOrderColumn(sortSet.getColumn()) + " " + sortSet.getSortType().getOperator();
                })
                .collect(Collectors.joining(CommonConstant.COMMA_MARK));
    }

    protected String concatOrderColumn(String column) {
        String alias = findTableAliasOfColumn(column);

        if (StringUtil.isEmpty(alias)) {
            return column;
        }

        return alias + ".`" + column + "` ";
    }

    /**
     * 查找列所在的表别名
     * @param column
     * @return
     */
    private String findTableAliasOfColumn(final String column) {
        Optional<TableFieldMetadata> findOnLeftOptional = findTableFieldMetadata(column, leftRepository);

        StandardJpaRepository rightRepository = repositoryRegister.findRegisteredRepositoryOfEntity(rightEntityType);

        Optional<TableFieldMetadata> findOnRightOptional = findTableFieldMetadata(column, rightRepository);

        if (findOnLeftOptional.isPresent() && findOnRightOptional.isPresent()) {
            log.error("the column `{}` exist in both tables {},{}", column, getLeftTableName(), getRightTableName());
            throw new XjpaExecuteException("ambiguous order column: " + column);
        } else if (findOnLeftOptional.isPresent()) {
            return LEFT;
        } else if (findOnRightOptional.isPresent()) {
            return RIGHT;
        }
        return CommonConstant.EMPTY_STRING;
    }

    /**
     * @param column find field
     * @param repository find on
     * @return
     */
    private Optional<TableFieldMetadata> findTableFieldMetadata(String column, StandardJpaRepository repository) {
        if (repository == null) {
            return Optional.empty();
        }
        RepositoryController controller = new DefaultRepositoryController(repository);
        TableMetadata tableFieldMetadata = controller.getTableFieldMetadata();
        return tableFieldMetadata.tableFields().stream()
                .filter(tf -> tf.getField().equalsIgnoreCase(column))
                .findAny();
    }


    private String formatLimitString() {
        LimitHandler limitHandler = enhanceCurdBound.getLimitHandler();
        if (!limitHandler.requireLimit()) {
            return null;
        }
        return limitHandler.getStart() + CommonConstant.COMMA_MARK + limitHandler.getLimit();

    }
}
