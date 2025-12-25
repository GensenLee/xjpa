package com.glee.xjpa.join;

import com.glee.xjpa.repository.StandardJpaRepository;
import com.glee.xjpa.repository.impl.enhance.EnhanceCurdBound;
import com.glee.xjpa.sql.where.operate.Condition;
import com.glee.xjpa.sql.where.operate.WhereOperator;
import com.glee.xjpa.sql.executor.SortType;
import com.glee.xjpa.sql.where.usermodel.XQueryWhereString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author GENSEN
 * @date 2023/6/20
 * @description
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractJoinModel implements JoinModel {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractJoinModel.class);

    protected final StandardJpaRepository repository;

    protected final Map<TableColumn, TableColumn> onFieldMapping = new HashMap<>();

    /**
     * 根条件
     */
    protected final JoinQueryWhere rootWhere;

    protected final EnhanceCurdBound enhanceCurdBound;

    protected final List<TableColumn> includedTableColumns = new ArrayList<>();

    protected final List<TableColumn> distinctTableColumns = new ArrayList<>();

    protected final List<TableColumn> groupingTableColumns = new ArrayList<>();


    protected AbstractJoinModel(StandardJpaRepository repository, JoinQueryWhere rootWhere,
                                EnhanceCurdBound enhanceCurdBound) {
        this.repository = repository;
        this.rootWhere = rootWhere;
        this.enhanceCurdBound = enhanceCurdBound;
    }

    abstract String getTableAlisa(JoinTable joinTable);


    /**
     * @return 连接符
     */
    abstract String getJoinLabel();

    @Override
    public JoinModel include(TableColumn... columns) {
        includedTableColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public JoinModel groupByColumns(TableColumn... columns) {
        groupingTableColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public JoinModel ignoreSoftDelete() {
        enhanceCurdBound.ignoreSoftDelete();
        return this;
    }

    @Override
    public JoinModel groupByColumns(String... columns) {
        enhanceCurdBound.groupByColumns(columns);
        return this;
    }

    @Override
    public JoinModel having(String havingString) {
        enhanceCurdBound.having(havingString);
        return this;
    }

    @Override
    public JoinModel distinct(String... columns) {
        enhanceCurdBound.distinct(columns);
        return this;
    }

    @Override
    public JoinModel distinct(TableColumn... columns) {
        distinctTableColumns.addAll(Arrays.asList(columns));
        return this;
    }

    @Override
    public JoinModel include(String... columns) {
        enhanceCurdBound.include(columns);
        return this;
    }

    @Override
    public JoinModel exclude(String... columns) {
        throw new UnsupportedOperationException("exclude");
    }

    @Override
    public JoinModel limit(int size) {
        enhanceCurdBound.limit(size);
        return this;
    }

    @Override
    public JoinModel limit(int start, int size) {
        enhanceCurdBound.limit(start, size);
        return this;
    }

    @Override
    public JoinModel limit(boolean isPage, int start, int size) {
        enhanceCurdBound.limit(isPage, start, size);
        return this;
    }

    @Override
    public JoinModel orderString(String orderByString) {
        enhanceCurdBound.orderString(orderByString);
        return this;
    }

    @Override
    public JoinModel orderByColumn(String column, SortType sortType) {
        enhanceCurdBound.orderByColumn(column, sortType);
        return this;
    }

    @Override
    public JoinModel orderByColumn(TableColumn column, SortType sortType) {
        if (column instanceof JoinTableVisitor) {
            ((JoinTableVisitor) column).visit(this);
        }
        enhanceCurdBound.orderByColumn(column.getColumnLabel(), sortType);
        return this;
    }


    @Override
    public JoinModel on(String leftColumn, String rightColumn) {
        onFieldMapping.put(ColumnDef.ofLeft(leftColumn), ColumnDef.ofRight(rightColumn));
        return this;
    }

    @Override
    public JoinModel on(TableColumn column1, TableColumn column2) {
        onFieldMapping.put(column1, column2);
        return this;
    }

    @Override
    public JoinModel where(String whereString) {
        rootWhere.put(new XQueryWhereString(whereString));
        return this;
    }

    @Override
    public JoinModel where(JoinQueryWhere joinQueryWhere, Condition condition) {
        rootWhere.put(joinQueryWhere.condition(condition));
        return this;
    }

    @Override
    public JoinModel where(TableColumn column, Object value, WhereOperator operator, Condition condition) {
        rootWhere.add(column, value, operator, condition);
        return this;
    }

}
