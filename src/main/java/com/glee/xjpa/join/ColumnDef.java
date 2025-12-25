package com.glee.xjpa.join;

/**
 * 列定义
 * @author GENSEN
 * @date 2023/6/21
 * @description
 */
public abstract class ColumnDef {

    private ColumnDef() {
    }

    /**
     * 用于include(....)参数
     *
     * @param column
     * @param alias
     * @return
     */
    public static AliasTableColumn aliasLeft(String column, String alias) {
        return new DefaultAliasTableColumn(JoinTable.LEFT, column, alias);
    }

    /**
     * 用于include(....)参数
     * @param column
     * @param alias
     * @return
     */
    public static AliasTableColumn aliasRight(String column, String alias) {
        return new DefaultAliasTableColumn(JoinTable.RIGHT, column, alias);
    }


    public static TableColumn ofLeft(String column) {
        return new JoinTableColumn(JoinTable.LEFT, column);
    }

    public static TableColumn ofRight(String column) {
        return new JoinTableColumn(JoinTable.RIGHT, column);
    }

    public static TableColumn countLeft(String column) {
        return new CountingJoinTableColumn(JoinTable.LEFT, column);
    }

    public static TableColumn countRight(String column) {
        return new CountingJoinTableColumn(JoinTable.RIGHT, column);
    }

    public static TableColumn countLeft(String column, String alias) {
        return new AliasCountJoinTableColumn(JoinTable.LEFT, column, alias);
    }

    public static TableColumn countRight(String column, String alias) {
        return new AliasCountJoinTableColumn(JoinTable.RIGHT, column, alias);
    }

    public static TableColumn sumLeft(String column, String alias) {
        return new AliasSumJoinTableColumn(JoinTable.LEFT, column, alias);
    }

    public static TableColumn sumRight(String column, String alias) {
        return new AliasSumJoinTableColumn(JoinTable.RIGHT, column, alias);
    }

    /**
     * 自定义语句段，lt.id 为左表id，rt.id 为右表id
     * @param str
     * @return
     */
    public static TableColumn plain(String str) {
        return new PlainTableColumn(str);
    }

}
