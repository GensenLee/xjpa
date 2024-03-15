package org.devops.data.xjpa.join;

import org.devops.data.xjpa.repository.IEnhanceRepository;
import org.devops.data.xjpa.sql.executor.SortType;
import org.devops.data.xjpa.sql.where.operate.Condition;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author GENSEN
 * @date 2023/6/20
 * @description 连接接口
 */
@SuppressWarnings("rawtypes")
public interface JoinModel extends IEnhanceRepository {

    /**
     * @param columns 使用工具类 ColumnDef 的定义列对象
     * @return
     */
    JoinModel include(TableColumn... columns);

    /**
     * @param columns 使用工具类 ColumnDef 的定义列对象
     * @return
     */
    JoinModel groupByColumns(TableColumn... columns);

    /**
     * 连接条件 on leftColumn = rightColumn
     *
     * @param leftColumn 左表列
     * @param rightColumn 右表列
     * @return
     */
    JoinModel on(String leftColumn, String rightColumn);

    /**
     * 连接条件 on column1 = column2
     *
     * @param column1
     * @param column2
     * @return
     */
    JoinModel on(TableColumn column1, TableColumn column2);

    JoinModel where(String whereString);

    JoinModel where(JoinQueryWhere joinQueryWhere, Condition condition);

    default JoinModel where(JoinQueryWhere joinQueryWhere){
        return where(joinQueryWhere, Condition.AND);
    }

    /**
     * @param column 使用工具类 ColumnDef 的定义列对象
     * @param value 值亦可为 TableColumn 类型对象，即 leftColumn (=/>/<) rightColumn 类型条件
     * @param operator
     * @param condition
     * @return
     */
    <T extends Serializable> JoinModel where(TableColumn column, T value, WhereOperator operator, Condition condition);

    default <T extends Serializable>  JoinModel where(TableColumn column, T value, WhereOperator operator){
        return where(column, value, operator, Condition.AND);
    }

    default <T extends Serializable> JoinModel where(TableColumn column, T value){
        return where(column, value, WhereOperator.EQ, Condition.AND);
    }

    /**
     * @return
     */
    List<Map<String, Object>> list();

    <T> List<T> list(Class<T> type);

    long count();

    boolean isExist();

    <T> List<T> listSingleColumn(Class<T> clazz);

    <T> T getSingleColumn(Class<T> clazz);

    default String getStringValue(){
        return getSingleColumn(String.class);
    }

    default Integer getIntegerValue(){
        return getSingleColumn(Integer.class);
    }

    default Long getLongValue(){
        return getSingleColumn(Long.class);
    }

    default List<String> listStringValue(){
        return listSingleColumn(String.class);
    }

    default List<Integer> listIntegerValue(){
        return listSingleColumn(Integer.class);
    }

    default List<Long> listLongValue(){
        return listSingleColumn(Long.class);
    }

    @Override
    JoinModel ignoreSoftDelete();

    @Override
    JoinModel groupByColumns(String... columns);

    @Override
    JoinModel having(String havingString);

    @Override
    JoinModel distinct(String... columns);

    JoinModel distinct(TableColumn... columns);

    @Override
    JoinModel include(String... columns);

    @Override
    JoinModel exclude(String... columns);

    @Override
    JoinModel limit(int size);

    @Override
    JoinModel limit(int start, int size);

    @Override
    JoinModel limit(boolean isPage, int start, int size);

    @Override
    JoinModel orderString(String orderByString);

    @Override
    JoinModel orderByColumn(String column, SortType sortType);

    /**
     * @param column 使用工具类 ColumnDef 的定义列对象
     * @param sortType
     * @return
     */
    JoinModel orderByColumn(TableColumn column, SortType sortType);

}
